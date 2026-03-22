package com.example.DDac_group18.controllers;

import com.example.DDac_group18.model.data_schema.EventAd;
import com.example.DDac_group18.services.EventAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Base64;
import java.util.List;

@Controller
public class EventDisplayController {

    @Autowired
    private EventAdService eventAdService;

    @GetMapping("/events-list")
    public String showEvents(Model model) {
        List<EventAd> events = eventAdService.findAll();

        // Convert each EventAd's imageData byte array to Base64 Data-URI and write it
        // to the image field
        for (EventAd e : events) {
            if (e.getImageData() != null && e.getImageData().length > 0) {
                String base64 = Base64.getEncoder().encodeToString(e.getImageData());
                e.setImage("data:image/jpeg;base64," + base64);
            }
        }

        model.addAttribute("events", events);
        return "doctor/events-list";
    }

    @GetMapping("/patient/events")
    public String showPatientEvents(Model model) {
        List<EventAd> events = eventAdService.findAll();

        // Convert each EventAd's imageData byte array to Base64 Data-URI and write it
        // to the image field
        for (EventAd e : events) {
            if (e.getImageData() != null && e.getImageData().length > 0) {
                String base64 = Base64.getEncoder().encodeToString(e.getImageData());
                e.setImage("data:image/jpeg;base64," + base64);
            }
        }

        model.addAttribute("events", events);
        return "patient/events-list";
    }

    @GetMapping("/dietician/events")
    public String showDieticianEvents(Model model) {
        List<EventAd> events = eventAdService.findAll();

        // Convert each EventAd's imageData byte array to Base64 Data-URI and write it
        // to the image field
        for (EventAd e : events) {
            if (e.getImageData() != null && e.getImageData().length > 0) {
                String base64 = Base64.getEncoder().encodeToString(e.getImageData());
                e.setImage("data:image/jpeg;base64," + base64);
            }
        }

        model.addAttribute("events", events);
        return "dietician/events-list";
    }
}
