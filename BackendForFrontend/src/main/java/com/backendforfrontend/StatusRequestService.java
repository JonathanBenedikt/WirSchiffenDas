package com.backendforfrontend;

import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.web.client.RestTemplate;

public class StatusRequestService {

    public StatusRequestService(String url)
    {
        this.url = url;
    }
    String url;

    @Retry(name="fetchStatusRetry")
    public String fetchStatus()
    {
        System.out.println("Aufruf von fetchStatus");
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url, String.class);
    }

    public String statusRequestFallback(Exception e)
    {
        return "The Host is unfortunately unreachable!";
    }

}
