package com.example.DDac_group18.controllers;

import com.example.DDac_group18.model.data_schema.EventAd;
import com.example.DDac_group18.services.EventAdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/event-ads")
public class EventAdsController {

    @Autowired
    private EventAdService service;

    @GetMapping
    public String list(@RequestParam(required = false) Long editId, Model model) {
        List<EventAd> events = service.findAll();

        // —— This is step 2: Convert the imageData of each record to Base64 and write it to the image field. ——
        for (EventAd e : events) {
            byte[] data = e.getImageData();
            if (data != null && data.length > 0) {
                String base64 = Base64.getEncoder().encodeToString(data);
                e.setImage("data:image/jpeg;base64," + base64);
            }
        }
        // ——————————————————————————————————————————————

        model.addAttribute("events", events);

        if (editId != null) {
            model.addAttribute("eventAd", service.findById(editId).orElse(new EventAd()));
            model.addAttribute("openModal", true);
        } else {
            model.addAttribute("eventAd", new EventAd());
        }
        return "doctor/event-ads";
    }

    @PostMapping("/save")
    public String save(
            @ModelAttribute EventAd eventAd,
            @RequestParam("imageFile") MultipartFile file
    ) throws IOException {
        if (file != null && !file.isEmpty()) {
            eventAd.setImageData(file.getBytes());
        }
        service.save(eventAd);
        return "redirect:/event-ads";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id) {
        return "redirect:/event-ads?editId=" + id;
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteById(id);
        return "redirect:/event-ads";
    }
}
