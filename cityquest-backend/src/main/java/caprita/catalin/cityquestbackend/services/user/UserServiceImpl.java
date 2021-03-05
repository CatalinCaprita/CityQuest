package caprita.catalin.cityquestbackend.services.user;

import caprita.catalin.cityquestbackend.domain.entities.User;
import caprita.catalin.cityquestbackend.repositories.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

@Service
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private  final ModelMapper modelMapper;
    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public User findById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User create(User user) {
        User exists =  userRepository.getByUsername(user.getUsername());
        if(exists == null){
            return userRepository.save(user);
        }
        user.setId(exists.getId());
        return  userRepository.save(user);

    }
}
