package com.driver.services.impl;

import com.driver.model.Admin;
import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.repository.AdminRepository;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.services.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    AdminRepository adminRepository1;

    @Autowired
    ServiceProviderRepository serviceProviderRepository1;

    @Autowired
    CountryRepository countryRepository1;

    @Override
    public Admin register(String username, String password) {
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);
        adminRepository1.save(admin);

        return admin;
    }

    @Override
    public Admin addServiceProvider(int adminId, String providerName) {
        //getting admin from repo with adminId
        Admin admin = adminRepository1.findById(adminId).get();

        //creating service provider
        ServiceProvider serviceProvider = new ServiceProvider();

        //setting attr in service provider
        serviceProvider.setName(providerName);
        serviceProvider.setAdmin(admin);

        //getting and setting attr in parent class Admin service provider list
        List<ServiceProvider> serviceProviders = admin.getServiceProviders();
        serviceProviders.add(serviceProvider);

        //saving parent class in repo
        adminRepository1.save(admin);

        return admin;

    }

    @Override
    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception{
        //getting service provider from repo with service provider id;
        ServiceProvider serviceProvider = serviceProviderRepository1.findById(serviceProviderId).get();

        //creating county obj
        Country country = new Country();

        //setting country obj attr


        if(!isValid(countryName)){
            throw new Exception("Country not found");
        }

        CountryName countryName1 = CountryName.valueOf(countryName.toUpperCase());
        country.setCountryName(countryName1);
        country.setCode(countryName1.toCode());
        //setting foreign key
        country.setServiceProvider(serviceProvider);

        //setting bidirectional mapping in service provider;
        List<Country> countryList = serviceProvider.getCountryList();
        countryList.add(country);

        //saving parent class
        serviceProviderRepository1.save(serviceProvider);

        return serviceProvider;

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
