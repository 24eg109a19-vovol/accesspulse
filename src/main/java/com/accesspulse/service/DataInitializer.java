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

        // 2. Seed Complete Campus Locations (A through I Blocks, Workshops, Facilities)
        boolean needsCampusUpdate = locationRepository.findAll().stream()
                .noneMatch(loc -> "E Block".equals(loc.getBuildingName()));

        if (locationRepository.count() == 0 || needsCampusUpdate) {
            if (needsCampusUpdate && locationRepository.count() > 0) {
                log.info("Migrating existing locations to complete campus layout...");
                complaintRepository.deleteAll();
                locationRepository.deleteAll();
            }

            log.info("Seeding institutional campus blocks and facilities...");

            // --- 1. A Block (Admin, CSE, EEE, Mechanical, Chemical, Info) ---
            locationRepository.save(Location.builder()
                    .buildingName("A Block")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Central Administration & Dean's Office")
                    .landmark("Main Admin Courtyard Entrance")
                    .importanceScore(5)
                    .accessibilityFeatures("Step-free entrance ramp, automatic doors, braille directory")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("A Block")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Computer Science and Engineering (CSE)")
                    .landmark("East Wing Corridor & CSE Labs")
                    .importanceScore(4)
                    .accessibilityFeatures("High-speed elevator access, wide lab aisles")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("A Block")
                    .floorOrLevel("2nd Floor")
                    .zoneOrRoom("Electrical and Electronics Engineering (EEE)")
                    .landmark("Central Corridor & EEE Faculty Rooms")
                    .importanceScore(4)
                    .accessibilityFeatures("Elevator access, tactile door signage")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("A Block")
                    .floorOrLevel("3rd Floor")
                    .zoneOrRoom("Mechanical Engineering")
                    .landmark("West Wing Design Labs & Classrooms")
                    .importanceScore(3)
                    .accessibilityFeatures("Elevator access, dual handrail stairwells")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("A Block")
                    .floorOrLevel("4th Floor")
                    .zoneOrRoom("Chemical Engineering & Information Departments")
                    .landmark("North Wing Research Labs")
                    .importanceScore(3)
                    .accessibilityFeatures("Elevator access, emergency eyewash stations")
                    .build());

            // --- 2. B Block (Pharmacy: Dept, Classrooms, Labs) ---
            locationRepository.save(Location.builder()
                    .buildingName("B Block")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Pharmacy Research & Instrumentation Laboratories")
                    .landmark("Main Lab Complex Entrance")
                    .importanceScore(4)
                    .accessibilityFeatures("Non-slip anti-static flooring, ramp access")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("B Block")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Pharmacy Department Offices & Classrooms")
                    .landmark("East Corridor Staircase & Elevator")
                    .importanceScore(3)
                    .accessibilityFeatures("Elevator access, braille room indicators")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("B Block")
                    .floorOrLevel("2nd Floor")
                    .zoneOrRoom("Pharmacy Classrooms & Seminar Hall")
                    .landmark("Upper Lecture Gallery")
                    .importanceScore(3)
                    .accessibilityFeatures("Elevator access, wheelchair designated desks")
                    .build());

            // --- 3. C Block (Civil Engineering: Dept, Labs, Classrooms) ---
            locationRepository.save(Location.builder()
                    .buildingName("C Block")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Civil Engineering Structural & Concrete Labs")
                    .landmark("Heavy Testing Yard")
                    .importanceScore(4)
                    .accessibilityFeatures("Double-width industrial doors, ground-level entry")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("C Block")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Civil Engineering Department & Faculty Chambers")
                    .landmark("Central Stairwell & Ramp Way")
                    .importanceScore(3)
                    .accessibilityFeatures("Accessible ramp, elevator access")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("C Block")
                    .floorOrLevel("2nd Floor")
                    .zoneOrRoom("Classrooms & CAD Drafting Halls")
                    .landmark("Upper Corridor")
                    .importanceScore(3)
                    .accessibilityFeatures("Elevator access, wide walkways")
                    .build());

            // --- 4. D Block (First-Year: Classrooms, Labs, Student Facilities) ---
            locationRepository.save(Location.builder()
                    .buildingName("D Block")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Common Student Facilities & First-Year Help Desk")
                    .landmark("Central Garden Entrance")
                    .importanceScore(4)
                    .accessibilityFeatures("Automated sliding doors, ramp access")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("D Block")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("First-Year Student Classrooms")
                    .landmark("North Corridor Classrooms D101-D110")
                    .importanceScore(4)
                    .accessibilityFeatures("Elevator access, braille floor indicators")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("D Block")
                    .floorOrLevel("2nd Floor")
                    .zoneOrRoom("First-Year Physics, Chemistry & Basic Computing Labs")
                    .landmark("South Wing Laboratories")
                    .importanceScore(4)
                    .accessibilityFeatures("Elevator access, accessible emergency wash")
                    .build());

            // --- 5. E Block (ECE, Exam Branch, Placement Cell, Admin) ---
            locationRepository.save(Location.builder()
                    .buildingName("E Block")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Examination Branch & Records Office")
                    .landmark("Main Student Verification Lobby")
                    .importanceScore(5)
                    .accessibilityFeatures("Accessible queue barriers, low-height enquiry counter")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("E Block")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Training & Placement Cell / Corporate Relations")
                    .landmark("Central Auditorium Entrance")
                    .importanceScore(4)
                    .accessibilityFeatures("Elevator access, interview rooms with ramp entry")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("E Block")
                    .floorOrLevel("2nd Floor")
                    .zoneOrRoom("Electronics and Communication Engineering (ECE)")
                    .landmark("ECE VLSI & Embedded Systems Labs")
                    .importanceScore(4)
                    .accessibilityFeatures("Elevator access, ESD-safe accessible flooring")
                    .build());

            // --- 6. F Block (Examination Counter) ---
            Location fBlockExamCounter = locationRepository.save(Location.builder()
                    .buildingName("F Block")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Examination Counter")
                    .landmark("Main Student Services & Exam Section")
                    .importanceScore(5) // Critical high footfall
                    .accessibilityFeatures("Accessible ramp, low-height service counter window, priority queue aisle")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("F Block")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Evaluation & Confidential Records Section")
                    .landmark("Exam Control Office")
                    .importanceScore(4)
                    .accessibilityFeatures("Elevator access, secured braille door access")
                    .build());

            // --- 7. G Block (Academic & Research Block) ---
            locationRepository.save(Location.builder()
                    .buildingName("G Block")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Academic Classrooms & Research Labs")
                    .landmark("North Quadrangle Entrance")
                    .importanceScore(3)
                    .accessibilityFeatures("Ramp access, tactile guide path")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("G Block")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Faculty Research Chambers & Classrooms")
                    .landmark("East Wing Corridor")
                    .importanceScore(3)
                    .accessibilityFeatures("Elevator access")
                    .build());

            // --- 8. H Block (All 7 Floors are Classes) ---
            Location hBlockFloor1 = null;
            for (int floor = 1; floor <= 7; floor++) {
                Location hLoc = locationRepository.save(Location.builder()
                        .buildingName("H Block")
                        .floorOrLevel(floor + getOrdinalSuffix(floor) + " Floor")
                        .zoneOrRoom("Classrooms (Rooms H" + floor + "01 - H" + floor + "10)")
                        .landmark("Elevator Lobby & Academic Corridor")
                        .importanceScore(floor == 1 ? 4 : 3)
                        .accessibilityFeatures("High-capacity elevator with braille/audio alerts, wide classroom doors")
                        .build());
                if (floor == 1) {
                    hBlockFloor1 = hLoc;
                }
            }

            // --- 9. I Block (1st Floor Canteen, remaining 6 floors Classes) ---
            Location iBlockCanteen = locationRepository.save(Location.builder()
                    .buildingName("I Block")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Campus Canteen & Cafeteria")
                    .landmark("Central Dining Area & Food Court")
                    .importanceScore(5) // Critical high footfall
                    .accessibilityFeatures("Wide dining aisles, step-free access ramp, designated wheelchair seating")
                    .build());

            for (int floor = 2; floor <= 7; floor++) {
                locationRepository.save(Location.builder()
                        .buildingName("I Block")
                        .floorOrLevel(floor + getOrdinalSuffix(floor) + " Floor")
                        .zoneOrRoom("Classrooms (Rooms I" + floor + "01 - I" + floor + "10)")
                        .landmark("Elevator & Staircase Lobby")
                        .importanceScore(4)
                        .accessibilityFeatures("Dual elevators with audio alerts, braille signage")
                        .build());
            }

            // --- 10. Workshop Sheds ---
            locationRepository.save(Location.builder()
                    .buildingName("Workshop Sheds")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Mechanical Workshops & Machine Shop")
                    .landmark("Foundry & Fitting Section")
                    .importanceScore(4)
                    .accessibilityFeatures("Ground-level industrial entry, safety guard paths")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("Workshop Sheds")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Practical Training Areas & Workshop Equipment")
                    .landmark("Welding & Electrical Bay")
                    .importanceScore(3)
                    .accessibilityFeatures("Level access, emergency stop pull cords")
                    .build());

            // --- 11. Sports Shed & Facilities ---
            locationRepository.save(Location.builder()
                    .buildingName("Sports Shed")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Indoor Sports Arena & Equipment Bay")
                    .landmark("Basketball & Badminton Courts")
                    .importanceScore(4)
                    .accessibilityFeatures("Accessible changing facilities, step-free entry")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("Sports Facilities")
                    .floorOrLevel("Ground Level")
                    .zoneOrRoom("Outdoor Athletic Grounds & Sports Courts")
                    .landmark("Main Campus Sports Complex")
                    .importanceScore(3)
                    .accessibilityFeatures("Accessible perimeter pathway, designated seating")
                    .build());

            // --- 12. Central Library ---
            locationRepository.save(Location.builder()
                    .buildingName("Library")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Main Reading Room & Issue Counter")
                    .landmark("Central Campus Atrium")
                    .importanceScore(5)
                    .accessibilityFeatures("Wheelchair ramp, automated entrance doors, tactile signage")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("Library")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Digital Library & Computer Study Lab")
                    .landmark("E-Learning Resource Section")
                    .importanceScore(4)
                    .accessibilityFeatures("Elevator access, adjustable height computer desks")
                    .build());

            // --- 13. Hostels ---
            locationRepository.save(Location.builder()
                    .buildingName("Hostels")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("Student Accommodation - Common Areas & Dining Hall")
                    .landmark("Hostel Complex Block A Entry")
                    .importanceScore(4)
                    .accessibilityFeatures("Accessible ramp, ground-floor accessible rooms")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("Hostels")
                    .floorOrLevel("1st Floor")
                    .zoneOrRoom("Hostel Rooms & Restroom Facilities")
                    .landmark("Hostel Corridor North")
                    .importanceScore(4)
                    .accessibilityFeatures("Wide doorways, accessible plumbing fixtures")
                    .build());

            // --- 14. Health Centre ---
            locationRepository.save(Location.builder()
                    .buildingName("Health Centre")
                    .floorOrLevel("Ground Floor")
                    .zoneOrRoom("First-Aid & Emergency Medical Assistance Unit")
                    .landmark("Adjacent to Student Welfare Center")
                    .importanceScore(5)
                    .accessibilityFeatures("Direct ambulance bay access, barrier-free entrance, wheelchair availability")
                    .build());

            // --- 15. Campus Roads and Pathways ---
            locationRepository.save(Location.builder()
                    .buildingName("Campus Roads and Pathways")
                    .floorOrLevel("Ground Level")
                    .zoneOrRoom("Main Academic Walkway & Pedestrian Pathway")
                    .landmark("Connecting A Block to I Block")
                    .importanceScore(5)
                    .accessibilityFeatures("Tactile paving, continuous curb ramps, pedestrian lighting")
                    .build());
            locationRepository.save(Location.builder()
                    .buildingName("Campus Roads and Pathways")
                    .floorOrLevel("Ground Level")
                    .zoneOrRoom("Accessible Parking Bays & Entrance Arteries")
                    .landmark("Opposite Administration & Health Centre")
                    .importanceScore(4)
                    .accessibilityFeatures("Blue marked accessible parking, dropped curbs")
                    .build());

            log.info("Complete campus locations seeded successfully.");

            // 3. Seed Sample Initial Reports for the updated campus layout
            if (complaintRepository.count() == 0) {
                final Location examLoc = fBlockExamCounter;
                final Location canteenLoc = iBlockCanteen;
                final Location hClassLoc = hBlockFloor1;

                userRepository.findByEmail("student@accesspulse.edu").ifPresent(studentUser -> {
                    // Sample 1: F Block Examination Counter (Accessibility issue)
                    ComplaintRequest sample1 = new ComplaintRequest();
                    sample1.setTitle("Wheelchair Ramp Handrail Damaged near Examination Counter");
                    sample1.setCategory(ComplaintCategory.ACCESSIBILITY_PROBLEMS);
                    sample1.setLocationId(examLoc.getId());
                    sample1.setDescription("The metal handrail along the accessibility ramp leading to the F Block Examination Counter has come loose and poses a risk to students with reduced mobility.");
                    sample1.setAccessibilityImpact(5);
                    sample1.setPeopleAffected(5);
                    sample1.setSafetyRisk(4);
                    complaintService.createComplaint(sample1, studentUser);

                    // Sample 2: I Block 1st Floor Canteen (Cleanliness / Safety issue)
                    ComplaintRequest sample2 = new ComplaintRequest();
                    sample2.setTitle("Heavy Water Leakage & Slippery Floor at I Block Canteen Entrance");
                    sample2.setCategory(ComplaintCategory.WATER_PLUMBING);
                    sample2.setLocationId(canteenLoc.getId());
                    sample2.setDescription("AC condenser drain pipe is leaking onto the ramp entrance of the I Block 1st floor canteen, creating an active slip hazard during peak lunch hours.");
                    sample2.setAccessibilityImpact(4);
                    sample2.setPeopleAffected(5);
                    sample2.setSafetyRisk(4);
                    complaintService.createComplaint(sample2, studentUser);

                    // Sample 3: H Block Classes (Elevator issue)
                    if (hClassLoc != null) {
                        ComplaintRequest sample3 = new ComplaintRequest();
                        sample3.setTitle("Elevator Call Button Unresponsive in H Block");
                        sample3.setCategory(ComplaintCategory.ELEVATOR_ISSUES);
                        sample3.setLocationId(hClassLoc.getId());
                        sample3.setDescription("Elevator 1 serving the 7 floors of classrooms in H Block stops 2 inches below the floor threshold on the 1st floor, impeding wheelchair ingress.");
                        sample3.setAccessibilityImpact(4);
                        sample3.setPeopleAffected(4);
                        sample3.setSafetyRisk(3);
                        complaintService.createComplaint(sample3, studentUser);
                    }
                });
            }
        }

    }

    private String getOrdinalSuffix(int number) {
        if (number >= 11 && number <= 13) {
            return "th";
        }
        return switch (number % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }
}

