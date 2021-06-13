package caprita.catalin.cityquestbackend.security.service;

import caprita.catalin.cityquestbackend.domain.entities.user.Role;
import caprita.catalin.cityquestbackend.domain.entities.user.User;
import caprita.catalin.cityquestbackend.domain.enums.RoleCode;
import caprita.catalin.cityquestbackend.repositories.user.UserRepository;
import caprita.catalin.cityquestbackend.util.Constants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.stream.Collectors;

@Service
@Transactional
public class JPAUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public JPAUserDetailsService(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(s).orElse(null);
        if(u == null)
            throw new UsernameNotFoundException(String.format(Constants.Error.COULD_NOT_LOCATE_USER, s));

        UserPrincipal creds =  modelMapper.map(u, UserPrincipal.class);
        creds.setRoles(u.getRoles().stream().map(Role::getCode).map(RoleCode::toString).collect(Collectors.toSet()));
        return creds;
    }
}
