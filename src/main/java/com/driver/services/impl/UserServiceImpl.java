package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{

        //creating and setting country obj
        Country countryCode = new Country();
        if(!isValid(countryName)){
            throw new Exception("Country not found");
        }

        CountryName countryName1 = CountryName.valueOf(countryName.toUpperCase());
        countryCode.setCountryName(countryName1);
        countryCode.setCode(countryName1.toCode());

        //creating and setting user obj
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setConnected(false);
        user.setOriginalCountry(countryCode);
        user.setOriginalIp(countryCode.getCode()+"."+user.getId());

        //setting foreign key
        countryCode.setUser(user);

        countryRepository3.save(countryCode);

        return user;

    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {

        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        List<ServiceProvider> serviceProviderList = user.getServiceProviderList();
        serviceProviderList.add(serviceProvider);

        List<User> userList = serviceProvider.getUsers();
        userList.add(user);

        serviceProviderRepository3.save(serviceProvider);
        userRepository3.save(user);

        return user;

    }

    private boolean isValid(String countryName){
        for(CountryName countryName1: CountryName.values()){
            if(countryName1.name().equals(countryName)){
                return true;
            }
        }

        return false;
    }
}
