package com.accesspulse.service;

import com.accesspulse.dto.ComplaintRequest;
import com.accesspulse.dto.RegistrationRequest;
import com.accesspulse.entity.Location;
import com.accesspulse.entity.User;
import com.accesspulse.enums.ComplaintCategory;
import com.accesspulse.enums.Role;
import com.accesspulse.repository.ComplaintRepository;
import com.accesspulse.repository.LocationRepository;
import com.accesspulse.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final ComplaintRepository complaintRepository;
    private final UserService userService;
    private final ComplaintService complaintService;

    @Override
    public void run(String... args) {
        log.info("Checking database initialization for AccessPulse...");

        // 1. Seed Demo Users if not present
        if (userRepository.count() == 0) {
            log.info("Seeding demo accounts...");

            // Admin User
            RegistrationRequest adminReq = new RegistrationRequest();
            adminReq.setFullName("Campus Accessibility Director (Admin)");
            adminReq.setEmail("admin@accesspulse.edu");
            adminReq.setPassword("admin123");
            adminReq.setRole(Role.ROLE_ADMIN);
            adminReq.setDepartmentOrId("Office of Diversity & Accessibility");
            adminReq.setPhone("+1 (555) 019-2831");
            userService.registerUser(adminReq);

            // Maintenance Staff 1
            RegistrationRequest maint1Req = new RegistrationRequest();
            maint1Req.setFullName("Marcus Vance (Facilities Lead)");
            maint1Req.setEmail("maint@accesspulse.edu");
            maint1Req.setPassword("maint123");
            maint1Req.setRole(Role.ROLE_MAINTENANCE);
            maint1Req.setDepartmentOrId("Campus Facilities & Mechanical Ops");
            maint1Req.setPhone("+1 (555) 019-4421");
            userService.registerUser(maint1Req);

            // Maintenance Staff 2
            RegistrationRequest maint2Req = new RegistrationRequest();
            maint2Req.setFullName("Elena Rostova (Electrical & Elevators)");
            maint2Req.setEmail("elena.maint@accesspulse.edu");
            maint2Req.setPassword("maint123");
            maint2Req.setRole(Role.ROLE_MAINTENANCE);
            maint2Req.setDepartmentOrId("Elevator & Power Maintenance");
            maint2Req.setPhone("+1 (555) 019-8833");
            userService.registerUser(maint2Req);

            // Student User
            RegistrationRequest studentReq = new RegistrationRequest();
            studentReq.setFullName("Alex Morgan (Student)");
            studentReq.setEmail("student@accesspulse.edu");
            studentReq.setPassword("student123");
            studentReq.setRole(Role.ROLE_STUDENT);
            studentReq.setDepartmentOrId("STU-2024-8841");
            studentReq.setPhone("+1 (555) 019-7712");
            User student = userService.registerUser(studentReq);

            // Staff User
            RegistrationRequest staffReq = new RegistrationRequest();
            staffReq.setFullName("Prof. Jordan Lee (Faculty)");
            staffReq.setEmail("staff@accesspulse.edu");
            staffReq.setPassword("staff123");
            staffReq.setRole(Role.ROLE_STAFF);
            staffReq.setDepartmentOrId("Department of Computer Science");
            staffReq.setPhone("+1 (555) 019-9900");
            userService.registerUser(staffReq);

            log.info("Demo accounts seeded successfully.");
        }

        // 2. Seed Campus Locations if empty
        if (locationRepository.count() == 0) {
            log.info("Seeding campus accessibility locations...");

            Location loc1 = Location.builder()
                    .buildingName("Science & Engineering Complex")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("West Wing Main Elevator #2")
                    .landmark("Near Physics Lecture Hall 101")
                    .importanceScore(5) // critical high footfall
                    .accessibilityFeatures("Elevator access to 4 floors, braille signage")
                    .build();
            locationRepository.save(loc1);

            Location loc2 = Location.builder()
                    .buildingName("University Central Library")
                    .floorOrLevel("Exterior Entrance")
                    .zoneOrRoom("East Accessible Ramp")
                    .landmark("Opposite Student Quadrangle")
                    .importanceScore(5)
                    .accessibilityFeatures("Wheelchair ramp, automated push button")
                    .build();
            locationRepository.save(loc2);

            Location loc3 = Location.builder()
                    .buildingName("Student Union Center")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Universal All-Gender Restroom")
                    .landmark("Next to Cafeteria Food Court")
                    .importanceScore(4)
                    .accessibilityFeatures("Grab bars, wide turning radius, emergency pull cord")
                    .build();
            locationRepository.save(loc3);

            Location loc4 = Location.builder()
                    .buildingName("Arts & Humanities Pavilion")
                    .floorOrLevel("2nd Floor")
                    .zoneOrRoom("Auditorium B Entrance")
                    .landmark("Stairwell North East")
                    .importanceScore(3)
                    .accessibilityFeatures("Tactile paving pathway, dual handrails")
                    .build();
            locationRepository.save(loc4);

            Location loc5 = Location.builder()
                    .buildingName("Campus North Parking Structure")
                    .floorOrLevel("Level 1")
                    .zoneOrRoom("Accessible Blue Parking Bay 1-6")
                    .landmark("Directly facing Health Services Clinic")
                    .importanceScore(4)
                    .accessibilityFeatures("Van accessible, curb cut ramp")
                    .build();
            locationRepository.save(loc5);

            log.info("Campus locations seeded successfully.");

            // 3. Seed Sample Initial Report so dashboards have live data immediately
            if (complaintRepository.count() == 0) {
                userRepository.findByEmail("student@accesspulse.edu").ifPresent(studentUser -> {
                    ComplaintRequest sample1 = new ComplaintRequest();
                    sample1.setTitle("Elevator #2 Button Panel Defective & Jerky Motion");
                    sample1.setCategory(ComplaintCategory.BROKEN_ELEVATOR);
                    sample1.setLocationId(loc1.getId());
                    sample1.setDescription("Elevator 2 in the Science Complex is failing to respond to 3rd floor calls and stops unevenly relative to the floor threshold by 2 inches, presenting a trip hazard for wheelchairs.");
                    sample1.setAccessibilityImpact(5);
                    sample1.setPeopleAffected(5);
                    sample1.setSafetyRisk(4);
                    complaintService.createComplaint(sample1, studentUser);

                    ComplaintRequest sample2 = new ComplaintRequest();
                    sample2.setTitle("Heavy Construction Material Blocking East Accessible Ramp");
                    sample2.setCategory(ComplaintCategory.BLOCKED_RAMP);
                    sample2.setLocationId(loc2.getId());
                    sample2.setDescription("Scaffolding poles and discarded sandbags have narrowed the wheelchair access ramp below standard 36-inch clearance. Manual wheelchair users cannot pass.");
                    sample2.setAccessibilityImpact(4);
                    sample2.setPeopleAffected(4);
                    sample2.setSafetyRisk(4);
                    complaintService.createComplaint(sample2, studentUser);
                });
            }
        }
    }
}
