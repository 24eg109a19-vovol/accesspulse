package com.accesspulse.repository;

import com.accesspulse.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByOrderByBuildingNameAscFloorOrLevelAsc();
    List<Location> findByBuildingNameContainingIgnoreCase(String buildingName);
}
