package com.demo.activiti.common.service;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;

@Primary
@Service("userService")
public class UserServiceImpl implements UserService{

    /**
     * activiti7强依赖security 此处用户放行，或对接自己系统用户即可
     * @param username 用户编码
     * @return org.springframework.security.core.userdetails.UserDetails
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
           /* GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("ROLE_ACTIVITI_USER");
            authorities.add(grantedAuthority);*/
        return new User(username, "password", authorities);
    }
}
