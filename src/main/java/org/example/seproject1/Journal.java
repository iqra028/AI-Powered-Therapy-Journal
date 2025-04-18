package org.example.seproject1;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


public abstract class Journal {
    public abstract String writeEntry(String entry);
    public abstract String summarize(String entries);
}