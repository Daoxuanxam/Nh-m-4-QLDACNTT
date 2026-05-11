package qltv.com.service;

import qltv.com.entity.*;
import qltv.com.repository.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ReaderRepository readerRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, ReaderRepository readerRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.readerRepository = readerRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() { return userRepository.findAll(); }

    public Optional<User> findById(Long id) { return userRepository.findById(id); }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(User.Role.USER);
        user.setEnabled(true);
        User savedUser = userRepository.save(user);

        Reader reader = new Reader(
            savedUser,
            "DG" + String.format("%05d", savedUser.getId()),
            LocalDate.now(),
            LocalDate.now().plusYears(1)
        );
        readerRepository.save(reader);

        return savedUser;
    }

    public User saveUser(User user) {
        if (!user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public void deleteUser(Long id) { userRepository.deleteById(id); }

    public List<User> findByRole(User.Role role) {
        return userRepository.findAll().stream()
            .filter(u -> u.getRole() == role).toList();
    }

    public long countByRole(User.Role role) { return userRepository.countByRole(role); }
}