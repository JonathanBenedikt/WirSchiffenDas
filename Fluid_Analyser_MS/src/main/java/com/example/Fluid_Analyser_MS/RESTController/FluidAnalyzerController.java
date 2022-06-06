package com.example.Fluid_Analyser_MS.RESTController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
public class FluidAnalyzerController {

    @Autowired
    private ApplicationContext appContext;

    @GetMapping(path="/information")
    public String showInfo() {return "This is the FluidAnalyser";}

    @GetMapping
    public ResponseEntity<List<FluidData>> getData(){
        FluidData testData = new FluidData(1,"Testdata","Testdescription");
        List<FluidData> fluidData = Collections.singletonList(testData);
        return ResponseEntity.ok(fluidData);
    }

    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }

    private static class FluidData {
        private final int Id;
        private final String Name;
        private final String Description;

        public FluidData(int Id, String Name, String Description){
            this.Id = Id;
            this.Name = Name;
            this.Description = Description;
        }

        public int getId(){
            return Id;
        }

        public String getName(){
            return Name;
        }

        public String getDescription(){
            return Description;
        }
    }
}
