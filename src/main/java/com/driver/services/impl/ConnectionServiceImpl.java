package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{

        Connection connection = new Connection();
        User user = userRepository2.findById(userId).get();
        if(user.getConnected()){
            throw new Exception("Already connected");
        }
        else if(user.getOriginalCountry().equals(CountryName.valueOf(countryName.toUpperCase()))){
            return user;
        }
        else if(user.getServiceProviderList().size()==0){
            throw new Exception("Unable to connect");
        }else {
            List<ServiceProvider> serviceProviders = user.getServiceProviderList();
            int serviceProviderId = Integer.MAX_VALUE;
            for(ServiceProvider serviceProvider: serviceProviders){
                List<Country> countryList = serviceProvider.getCountryList();
                for(Country country: countryList){
                    if(serviceProvider.getId()<serviceProviderId && country.getCountryName().equals(CountryName.valueOf(countryName.toUpperCase()))){
                        serviceProviderId = serviceProvider.getId();
                    }
                }
            }
            user.setConnected(true);
            user.setMaskedIp(CountryName.valueOf(countryName.toUpperCase()).toCode()+"."+serviceProviderId+"."+userId);

            connection.setUser(user);
            connection.setServiceProvider(serviceProviderRepository2.findById(serviceProviderId).get());
            ServiceProvider serviceProvider = serviceProviderRepository2.findById(serviceProviderId).get();
            serviceProvider.getConnectionList().add(connection);

            user.getConnectionList().add(connection);

            serviceProviderRepository2.save(serviceProvider);
        }


        userRepository2.save(user);

        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if(!user.getConnected()){
            throw new Exception("Already disconnected");
        }else{
            user.setMaskedIp(null);
            user.setConnected(false);

        }

        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {
            User sender = userRepository2.findById(senderId).get();
            User receiver = userRepository2.findById(receiverId).get();

            if(receiver.getMaskedIp() != null){
                String countryCode = receiver.getMaskedIp().substring(0,3);
                if(sender.getOriginalCountry().getCountryName().equals(countryCode)){
                    return sender;
                }

            }else{
                if(sender.getOriginalCountry().getCountryName().equals(receiver.getOriginalCountry().getCountryName())){
                    return sender;
                }
            }

          sender = connect(senderId, receiver.getOriginalCountry().getCountryName().name());
            if(!sender.getConnected()){
                throw new Exception("Cannot establish communication");
            }

            return sender;

    }
}
