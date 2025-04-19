package com.example.clothingstore.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import com.example.clothingstore.model.User;
import com.example.clothingstore.repository.ArticleRepo;
import com.example.clothingstore.repository.UserRepo;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserRepo userRepo;

    private MutableLiveData<List<User>> userList;

    public UserViewModel(@NonNull Application application) {
        super(application);

        userRepo = UserRepo.getInstance();

        userList = userRepo.getUsers();
    }

    public void addNewUser(User user) {
        userRepo.addNewUser(user);
    }

    public void updateUser(int id, User user) {
        userRepo.updateUser(id, user);
    }

    public void deleteUser(int id){
        userRepo.deleteUser(id);
    }

    public MutableLiveData<List<User>> getUsers() {
        return userList;
    }


    // Method to handle login
    public MutableLiveData<User> loginUser(String email, String password) {
        return userRepo.loginUser(email, password);
    }


}
