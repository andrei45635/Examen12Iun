package org.example.rest.services;

import org.example.Configuration;
import org.example.repo.configurations.ConfigurationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/game/configurations")
public class ConfigurationsController {
    @Autowired
    private ConfigurationRepository configurationRepository;

    @RequestMapping(method = RequestMethod.POST)
    public void save(@RequestBody Configuration config) throws IOException {
        System.out.println("Adding the configuration " + config.toString());
        configurationRepository.save(config);
    }

    @RequestMapping(method = RequestMethod.GET)
    public List<Configuration> getAllConfigurations(){
        System.out.println("Getting all the configurations RESTfully");
        return configurationRepository.getAll();
    }

    @ExceptionHandler(ServiceException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String taskError(ServiceException e) {
        return e.getMessage();
    }
}
