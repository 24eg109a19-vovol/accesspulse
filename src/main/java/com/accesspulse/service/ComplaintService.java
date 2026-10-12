package com.accesspulse.service;

import com.accesspulse.dto.AssignStaffRequest;
import com.accesspulse.dto.ComplaintRequest;
import com.accesspulse.dto.FeedbackRequest;
import com.accesspulse.dto.PriorityUpdateRequest;
import com.accesspulse.dto.ResolutionRequest;
import com.accesspulse.entity.*;
import com.accesspulse.enums.ComplaintCategory;
import com.accesspulse.enums.ComplaintStatus;
import com.accesspulse.enums.PriorityLevel;
import com.accesspulse.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComplaintService {

        private final ComplaintRepository complaintRepository;
        private final LocationRepository locationRepository;
        private final AssignmentRepository assignmentRepository;
        private final ResolutionRepository resolutionRepository;
        private final StatusHistoryRepository statusHistoryRepository;
        private final FeedbackRepository feedbackRepository;
        private final PriorityService priorityService;
        private final FileStorageService fileStorageService;
        private final NotificationService notificationService;

        @Transactional
        public Complaint createComplaint(ComplaintRequest request, User reporter) {
                Location location = locationRepository.findById(request.getLocationId())
                                .orElseThrow(() -> new IllegalArgumentException("Invalid campus location selected"));

                String imageFilename = null;
                if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
                        imageFilename = fileStorageService.storeFile(request.getImageFile());
                }

                // Calculate initial priority score
                int impact = request.getAccessibilityImpact() > 0 ? request.getAccessibilityImpact()
                                : request.getCategory().getDefaultImpact();
                int safety = request.getSafetyRisk() > 0 ? request.getSafetyRisk()
                                : request.getCategory().getDefaultSafetyRisk();
                int people = request.getPeopleAffected() > 0 ? request.getPeopleAffected() : 3;
                int locImportance = location.getImportanceScore() > 0 ? location.getImportanceScore() : 3;

                double initialScore = priorityService.calculatePriorityScore(impact, people, safety, locImportance,
                                LocalDateTime.now());
                PriorityLevel priorityLevel = priorityService.determinePriorityLevel(initialScore);

                Complaint complaint = Complaint.builder()
                                .title(request.getTitle().trim())
                                .category(request.getCategory())
                                .location(location)
                                .description(request.getDescription().trim())
                                .imagePath(imageFilename)
                                .status(ComplaintStatus.SUBMITTED)
                                .accessibilityImpact(impact)
                                .peopleAffected(people)
                                .safetyRisk(safety)
                                .locationImportance(locImportance)
                                .priorityScore(initialScore)
                                .priorityLevel(priorityLevel)
                                .reporter(reporter)
                                .build();

                Complaint saved = complaintRepository.save(complaint);

                // Record initial status in history
                logStatusChange(saved, null, ComplaintStatus.SUBMITTED, reporter,
                                "Complaint submitted by " + reporter.getFullName());

                // Notify user
                notificationService.createNotification(
                                reporter,
                                "Report Submitted",
                                "Your report #" + saved.getId() + " (" + saved.getTitle()
                                                + ") has been submitted successfully.",
                                "/complaints/" + saved.getId());

                return saved;
        }

        public List<Complaint> getComplaintsByReporter(User reporter) {
                List<Complaint> list = complaintRepository.findByReporterOrderByCreatedAtDesc(reporter);
                // Refresh priorities based on unresolved age
                list.forEach(priorityService::updateComplaintPriority);
                return list;
        }

        public List<Complaint> getComplaintsAssignedTo(User staff) {
                List<Complaint> list = complaintRepository
                                .findByAssignedStaffOrderByPriorityScoreDescCreatedAtDesc(staff);
                list.forEach(priorityService::updateComplaintPriority);
                return list;
        }

        public List<Complaint> getAllComplaints() {
                List<Complaint> list = complaintRepository.findAllByOrderByPriorityScoreDescCreatedAtDesc();
                list.forEach(priorityService::updateComplaintPriority);
                return list;
        }

        public Optional<Complaint> getComplaintById(Long id) {
                Optional<Complaint> opt = complaintRepository.findById(id);
                opt.ifPresent(priorityService::updateComplaintPriority);
                return opt;
        }

        @Transactional
        public void verifyComplaint(Long complaintId, User admin, String adminNotes) {
                Complaint complaint = complaintRepository.findById(complaintId)
                                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

                ComplaintStatus oldStatus = complaint.getStatus();
                complaint.setStatus(ComplaintStatus.VERIFIED);
                if (adminNotes != null && !adminNotes.isBlank()) {
                        complaint.setAdminNotes(adminNotes);
                }
                complaintRepository.save(complaint);

                logStatusChange(complaint, oldStatus, ComplaintStatus.VERIFIED, admin,
                                "Complaint verified by Administrator: " + admin.getFullName());

                notificationService.createNotification(
                                complaint.getReporter(),
                                "Report Verified",
                                "Your report #" + complaint.getId() + " has been verified and marked for assignment.",
                                "/complaints/" + complaint.getId());
        }

        @Transactional
        public void rejectComplaint(Long complaintId, User admin, String reason) {
                Complaint complaint = complaintRepository.findById(complaintId)
                                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

                ComplaintStatus oldStatus = complaint.getStatus();
                complaint.setStatus(ComplaintStatus.REJECTED);
                complaint.setAdminNotes("Rejected: " + reason);
                complaintRepository.save(complaint);

                logStatusChange(complaint, oldStatus, ComplaintStatus.REJECTED, admin, "Rejected: " + reason);

                notificationService.createNotification(
                                complaint.getReporter(),
                                "Report Rejected",
                                "Your report #" + complaint.getId() + " was rejected. Reason: " + reason,
                                "/complaints/" + complaint.getId());
        }

        @Transactional
        public void updatePriority(PriorityUpdateRequest request, User admin) {
                Complaint complaint = complaintRepository.findById(request.getComplaintId())
                                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

                complaint.setAccessibilityImpact(request.getAccessibilityImpact());
                complaint.setPeopleAffected(request.getPeopleAffected());
                complaint.setSafetyRisk(request.getSafetyRisk());
                if (request.getAdminNotes() != null && !request.getAdminNotes().isBlank()) {
                        complaint.setAdminNotes(request.getAdminNotes());
                }

                priorityService.updateComplaintPriority(complaint);
                complaintRepository.save(complaint);

                logStatusChange(complaint, complaint.getStatus(), complaint.getStatus(), admin,
                                "Priority recalculated by Administrator. New Level: "
                                                + complaint.getPriorityLevel().name()
                                                + " (Score: " + complaint.getPriorityScore() + ")");
        }

        @Transactional
        public void assignComplaint(AssignStaffRequest request, User admin, User staff) {
                Complaint complaint = complaintRepository.findById(request.getComplaintId())
                                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

                ComplaintStatus oldStatus = complaint.getStatus();
                complaint.setStatus(ComplaintStatus.ASSIGNED);
                complaint.setAssignedStaff(staff);
                complaintRepository.save(complaint);

                Assignment assignment = Assignment.builder()
                                .complaint(complaint)
                                .assignedStaff(staff)
                                .assignedBy(admin)
                                .instructions(request.getInstructions())
                                .targetCompletionDate(request.getTargetCompletionDate())
                                .status("PENDING")
                                .build();
                assignmentRepository.save(assignment);

                logStatusChange(complaint, oldStatus, ComplaintStatus.ASSIGNED, admin,
                                "Assigned to Maintenance Staff: " + staff.getFullName() + ". Notes: "
                                                + (request.getInstructions() != null ? request.getInstructions()
                                                                : "N/A"));

                // Notify staff
                notificationService.createNotification(
                                staff,
                                "New Task Assigned",
                                "You have been assigned to resolve barrier #" + complaint.getId() + " at "
                                                + complaint.getLocation().getBuildingName(),
                                "/maintenance/complaints/" + complaint.getId());

                // Notify reporter
                notificationService.createNotification(
                                complaint.getReporter(),
                                "Staff Assigned",
                                "Maintenance staff (" + staff.getFullName() + ") has been assigned to your report #"
                                                + complaint.getId(),
                                "/complaints/" + complaint.getId());
        }

        @Transactional
        public void updateMaintenanceProgress(Long complaintId, ComplaintStatus newStatus, String remarks, User staff) {
                Complaint complaint = complaintRepository.findById(complaintId)
                                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

                ComplaintStatus oldStatus = complaint.getStatus();
                complaint.setStatus(newStatus);
                complaintRepository.save(complaint);

                // Update active assignment status
                assignmentRepository.findTopByComplaintOrderByAssignedAtDesc(complaint).ifPresent(a -> {
                        a.setStatus(newStatus.name());
                        assignmentRepository.save(a);
                });

                logStatusChange(complaint, oldStatus, newStatus, staff, remarks);

                notificationService.createNotification(
                                complaint.getReporter(),
                                "Progress Update",
                                "Status of report #" + complaint.getId() + " changed to " + newStatus.getDisplayName()
                                                + ". Remarks: "
                                                + remarks,
                                "/complaints/" + complaint.getId());
        }

        @Transactional
        public void submitResolution(ResolutionRequest request, User staff) {
                Complaint complaint = complaintRepository.findById(request.getComplaintId())
                                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

                String imageFilename = null;
                if (request.getCompletionImage() != null && !request.getCompletionImage().isEmpty()) {
                        imageFilename = fileStorageService.storeFile(request.getCompletionImage());
                }

                Resolution resolution = resolutionRepository.findByComplaint(complaint)
                                .orElse(Resolution.builder().complaint(complaint).build());

                resolution.setResolvedBy(staff);
                resolution.setCompletionNotes(request.getCompletionNotes().trim());
                if (imageFilename != null) {
                        resolution.setResolutionImagePath(imageFilename);
                }
                resolutionRepository.save(resolution);

                ComplaintStatus oldStatus = complaint.getStatus();
                complaint.setStatus(ComplaintStatus.RESOLVED);
                complaint.setResolvedAt(LocalDateTime.now());
                complaintRepository.save(complaint);

                assignmentRepository.findTopByComplaintOrderByAssignedAtDesc(complaint).ifPresent(a -> {
                        a.setStatus("COMPLETED");
                        a.setCompletedAt(LocalDateTime.now());
                        assignmentRepository.save(a);
                });

                logStatusChange(complaint, oldStatus, ComplaintStatus.RESOLVED, staff,
                                "Work completed by " + staff.getFullName() + ". Notes: "
                                                + request.getCompletionNotes());

                notificationService.createNotification(
                                complaint.getReporter(),
                                "Issue Resolved - Verification Requested",
                                "Your reported accessibility barrier #" + complaint.getId()
                                                + " has been marked RESOLVED. Please verify the work and submit your feedback.",
                                "/complaints/" + complaint.getId() + "/verify");
        }

        @Transactional
        public void verifyAndCloseComplaint(FeedbackRequest request, User reporter) {
                Complaint complaint = complaintRepository.findById(request.getComplaintId())
                                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));

                if (!complaint.getReporter().getId().equals(reporter.getId())) {
                        throw new IllegalArgumentException("Only the reporter can verify and close this complaint.");
                }

                Feedback feedback = feedbackRepository.findByComplaint(complaint)
                                .orElse(Feedback.builder().complaint(complaint).build());

                feedback.setUser(reporter);
                feedback.setRating(request.getRating());
                feedback.setResolutionSatisfactory(request.isResolutionSatisfactory());
                feedback.setComments(request.getComments());
                feedbackRepository.save(feedback);

                ComplaintStatus oldStatus = complaint.getStatus();
                if (request.isResolutionSatisfactory()) {
                        complaint.setStatus(ComplaintStatus.CLOSED);
                        complaint.setClosedAt(LocalDateTime.now());
                        logStatusChange(complaint, oldStatus, ComplaintStatus.CLOSED, reporter,
                                        "Resolution accepted and verified by reporter. Rating: " + request.getRating()
                                                        + "/5 stars.");
                } else {
                        // Re-open if student is dissatisfied
                        complaint.setStatus(ComplaintStatus.UNDER_REVIEW);
                        logStatusChange(complaint, oldStatus, ComplaintStatus.UNDER_REVIEW, reporter,
                                        "Resolution REJECTED by reporter (Dissatisfied). Feedback: "
                                                        + request.getComments());

                        // Notify Admin & Staff
                        if (complaint.getAssignedStaff() != null) {
                                notificationService.createNotification(
                                                complaint.getAssignedStaff(),
                                                "Resolution Rejected",
                                                "The reporter was not satisfied with the resolution on #"
                                                                + complaint.getId()
                                                                + ". It has been returned for review.",
                                                "/maintenance/complaints/" + complaint.getId());
                        }
                }
                complaintRepository.save(complaint);
        }

        public List<StatusHistory> getComplaintTimeline(Complaint complaint) {
                return statusHistoryRepository.findByComplaintOrderByTimestampAsc(complaint);
        }

        public Optional<Resolution> getResolution(Complaint complaint) {
                return resolutionRepository.findByComplaint(complaint);
        }

        public Optional<Feedback> getFeedback(Complaint complaint) {
                return feedbackRepository.findByComplaint(complaint);
        }

        public List<Location> getAllLocations() {
                return locationRepository.findAllByOrderByBuildingNameAscFloorOrLevelAsc();
        }

        @Transactional
        public Location saveLocation(Location location) {
                return locationRepository.save(location);
        }

        @Transactional
        public void deleteLocation(Long id) {
                Location location = locationRepository.findById(id)
                                .orElseThrow(() -> new IllegalArgumentException("Location not found with id: " + id));
                if (complaintRepository.existsByLocation(location)) {
                        throw new IllegalStateException("Cannot delete location because existing complaints are associated with it.");
                }
                locationRepository.delete(location);
        }

        public Map<String, Object> getSystemStatistics() {
                Map<String, Object> stats = new HashMap<>();
                long total = complaintRepository.count();
                long resolved = complaintRepository.countByStatus(ComplaintStatus.RESOLVED)
                                + complaintRepository.countByStatus(ComplaintStatus.CLOSED);
                long inProgress = complaintRepository.countByStatus(ComplaintStatus.IN_PROGRESS)
                                + complaintRepository.countByStatus(ComplaintStatus.ASSIGNED);
                long pending = complaintRepository.countByStatus(ComplaintStatus.SUBMITTED)
                                + complaintRepository.countByStatus(ComplaintStatus.UNDER_REVIEW)
                                + complaintRepository.countByStatus(ComplaintStatus.VERIFIED);
                long critical = complaintRepository.countByPriorityLevel(PriorityLevel.CRITICAL);
                long high = complaintRepository.countByPriorityLevel(PriorityLevel.HIGH);

                stats.put("total", total);
                stats.put("resolved", resolved);
                stats.put("inProgress", inProgress);
                stats.put("pending", pending);
                stats.put("critical", critical);
                stats.put("high", high);
                stats.put("resolutionRate", total > 0 ? Math.round(((double) resolved / total) * 100) : 0);
                return stats;
        }

        private void logStatusChange(Complaint complaint, ComplaintStatus oldStatus, ComplaintStatus newStatus,
                        User changedBy, String remarks) {
                StatusHistory history = StatusHistory.builder()
                                .complaint(complaint)
                                .oldStatus(oldStatus)
                                .newStatus(newStatus)
                                .changedBy(changedBy)
                                .remarks(remarks)
                                .build();
                statusHistoryRepository.save(history);
        }
}
