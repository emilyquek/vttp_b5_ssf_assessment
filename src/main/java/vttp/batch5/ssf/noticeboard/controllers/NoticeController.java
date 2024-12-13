package vttp.batch5.ssf.noticeboard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import jakarta.validation.Valid;
import vttp.batch5.ssf.noticeboard.models.Notice;
import vttp.batch5.ssf.noticeboard.services.NoticeService;

// Use this class to write your request handlers

@Controller
@RequestMapping
public class NoticeController {

    @Autowired
    private NoticeService noticeSvc;

    @GetMapping("/")
    public ModelAndView getLanding() {

        ModelAndView mav = new ModelAndView("notice");
        mav.addObject("notice", new Notice());
        return mav;
    }

    @PostMapping(path = "/notice", consumes = "application/x-www-form-urlencoded", produces = "application/json")
    public ModelAndView postNotice(@Valid @ModelAttribute Notice notice, BindingResult bindings) {
        
        ModelAndView mav = new ModelAndView();

        if (bindings.hasErrors()) {
            mav.setViewName("notice");
            return mav;
        }
        
        mav.addObject("notice", new Notice());

        String response = noticeSvc.postToNoticeServer(notice);
        // String[] respArray = response.split(",");
        String[] respArray = (response.split(","));
        String respId = respArray[0].replaceAll("\"", "").substring(4);

        if (noticeSvc.existingId(respId) == true) {
            mav.setViewName("successful");
            mav.addObject("id", respId);
            return mav;
        } else {
            mav.setViewName("failed");
            mav.addObject("message", respArray[0]);
            return mav;
        }
    }

    @GetMapping("/status")
    @ResponseBody
    public ResponseEntity<String> getHealthStatus() {
        
        String randomId = noticeSvc.getRandomId();

        if (noticeSvc.existingId(randomId) == true) {
            return ResponseEntity.status(200).contentType(MediaType.APPLICATION_JSON).body("{}");
        }
        
        return ResponseEntity.status(503).contentType(MediaType.APPLICATION_JSON).body("{}");
    }
}
