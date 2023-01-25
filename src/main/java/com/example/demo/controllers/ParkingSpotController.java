package com.example.demo.controllers;

import com.example.demo.dtos.PartkingSpotDTO;
import com.example.demo.models.ParkingSpotModel;
import com.example.demo.services.ParkingSpotService;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/parking-spot")
public class ParkingSpotController {
    final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }


    @PostMapping
    public ResponseEntity<Object> saveParkingSpot(@RequestBody @Valid PartkingSpotDTO parkingSpotDTO){

        if(parkingSpotService.existsByLicensePlateCar(parkingSpotDTO.getLicensePlateCar())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Conflict: License Plate Car is already in use!");
        }

        if(parkingSpotService.existsByParkingSpotNumber(parkingSpotDTO.getParkingSpotNumber())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Conflict: Parking Spot is already in use!");
        }

        if(parkingSpotService.existsByApartmentAndBlock(parkingSpotDTO.getApartment(), parkingSpotDTO.getBlock())){
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Conflict: Parking Spot already registered for this apartment/block!");
        }


        var parkingSpotModel = new ParkingSpotModel();
        BeanUtils.copyProperties(parkingSpotDTO, parkingSpotModel);
        parkingSpotModel.setRegistrationDate(LocalDateTime.now(ZoneId.of("UTC")));
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(parkingSpotService.save(parkingSpotModel));
    }

    @GetMapping
    public ResponseEntity<List<ParkingSpotModel>> getAllSparkingSpots(){
        return ResponseEntity.status(HttpStatus.OK)
                .body(parkingSpotService.findAll());
    }

}
