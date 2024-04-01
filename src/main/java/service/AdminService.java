package service;

import domain.Admin;
import org.mindrot.jbcrypt.BCrypt;
import repository.AdminRepository;

public class AdminService {

    private final AdminRepository repository;

    public AdminService(AdminRepository repository) {
        this.repository = repository;
    }

    public Admin login(String id, String password) {

        Admin findAdmin = repository.findById(id);

        if (findAdmin != null && BCrypt.checkpw(password, findAdmin.getPassword())) {
            return findAdmin;
        }
        return null;
    }
}