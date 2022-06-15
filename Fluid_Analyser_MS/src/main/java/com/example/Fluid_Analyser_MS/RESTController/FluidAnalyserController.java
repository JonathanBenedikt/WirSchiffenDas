package com.example.Fluid_Analyser_MS.RESTController;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.shared.Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@EnableEurekaClient
@RestController
public class FluidAnalyserController {

    @Autowired
    private EurekaClient eurekaClient;

    @Autowired
    private ApplicationContext appContext;

    @GetMapping(path="/information")
    public String showInfo() {return "Name: Fluid-Analyser\nType: Microservice\nVersion: 1.0.0";}

    @PostMapping(path="/analyse")
    public ResponseEntity<List<FluidInformation>> getData(@RequestBody FluidInformation providedFluid){
        try {
            List<FluidInformation> fluidData = Collections.singletonList(providedFluid);
            TimeUnit.SECONDS.sleep(ThreadLocalRandom.current().nextInt(5, 10));
            return ResponseEntity.ok(fluidData);
        }catch (Exception ex){
            System.out.println(ex);
        }
        return null;
    }
    @GetMapping(path="/shutdown")
    public void shutdown(){
        SpringApplication.exit(appContext, () -> 0);
    }

    public static class FluidInformation {

        private int id;
        private String name;
        private String description;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }
    }
}
