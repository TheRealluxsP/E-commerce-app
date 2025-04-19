package com.example.clothingstore.repository;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import com.example.clothingstore.model.Address;
import com.example.clothingstore.model.Article;
import com.example.clothingstore.model.User;
import com.example.clothingstore.retrofit.RetrofitApiInterface;
import com.example.clothingstore.retrofit.RetrofitClientInstance;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.observers.DisposableObserver;
import io.reactivex.rxjava3.schedulers.Schedulers;

import java.util.ArrayList;
import java.util.List;

public class UserRepo {

    private MutableLiveData<List<User>> userList;

    private static UserRepo instance;

    private RetrofitApiInterface service = RetrofitClientInstance.getRetrofitApiInterface();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static UserRepo getInstance() {
        if (instance == null) {
            instance = new UserRepo();
        }
        return instance;
    }

    private UserRepo() {
        //Initialize the MutableLiveData object
        userList = new MutableLiveData<List<User>>();

        fetchUsers();

    }

    private void fetchUsers() {

        Observable<List<User>> observable = service.getUsers();

        Observer<List<User>> observer = new Observer<List<User>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {}
            @Override
            public void onNext(@NonNull List<User> users) {
                userList.setValue(users);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("UserRepo", e.getMessage());
            }
            @Override
            public void onComplete() {}
        };

        observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(observer);
    }

    public void addNewUser(User user) {
        Observable<User> addUserObservable = service.addUser(user);
        DisposableObserver<User> addUserObserver = new DisposableObserver<User>() {
            @Override
            public void onNext(@NonNull User addedUser) {
                List<User> currentUserList = userList.getValue();
                List<User> updatedUserList = new ArrayList<>();
                if(currentUserList != null) {
                    updatedUserList.addAll(currentUserList);
                }
                updatedUserList.add(user);
                userList.setValue(updatedUserList);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("UserRepo", e.getMessage());
                e.printStackTrace();
            }
            @Override
            public void onComplete() {}
        };

        compositeDisposable.add(addUserObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(addUserObserver));
    }

    public void updateUser(int id, User user) {
        Observable<User> updateUserObservable = service.updateUser(id, user);
        DisposableObserver<User> updateUserObserver = new DisposableObserver<User>() {
            @Override
            public void onNext(@NonNull User updatedUser) {
                List<User> currentUserList = userList.getValue();

                if (currentUserList == null) {
                    return;
                }

                // Create a copy of the current user list to modify
                List<User> updatedUserList = new ArrayList<>(currentUserList);

                for (int i = 0; i < updatedUserList.size(); i++) {
                    User userForLoop = updatedUserList.get(i);
                    if (userForLoop.getId() == id) {
                        // Merge the updated fields with the existing user (because the api just returns the updated field, this is how it works)
                        if (updatedUser.getEmail() != null) userForLoop.setEmail(updatedUser.getEmail());
                        if (updatedUser.getPassword() != null) userForLoop.setPassword(updatedUser.getPassword());
                        if (updatedUser.getName() != null) userForLoop.setName(updatedUser.getName());
                        if (updatedUser.getPhone() != null) userForLoop.setPhone(updatedUser.getPhone());
                        if (updatedUser.getAddress() != null) {
                            Address address = userForLoop.getAddress();
                            Address updatedAddress = updatedUser.getAddress();
                            if (address != null) {
                                if (updatedAddress.getCity() != null) address.setCity(updatedAddress.getCity());
                                if (updatedAddress.getStreet() != null) address.setStreet(updatedAddress.getStreet());
                                if (updatedAddress.getNumber() != 0) address.setNumber(updatedAddress.getNumber());
                                if (updatedAddress.getZipcode() != null) address.setZipcode(updatedAddress.getZipcode());
                            }
                        }
                        break;
                    }
                }

                // Update the LiveData value with the modified list
                userList.setValue(updatedUserList);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("UserRepo", e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onComplete() {}
        };

        compositeDisposable.add(updateUserObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(updateUserObserver));
    }


    public void deleteUser(int userId){
        Observable<User> deleteUserObservable = service.deleteUser(userId);
        DisposableObserver<User> deleteUserObserver = new DisposableObserver<User>(){
            @Override
            public void onNext(@NonNull User deletedUser) {
                List<User> currentUserList = userList.getValue();

                if(currentUserList == null) {
                    return;
                }

                List<User> updatedUserList = new ArrayList<>();
                for(User user : currentUserList){
                    if(user.getId() != userId){
                        updatedUserList.add(user);
                    }
                }
                userList.setValue(updatedUserList);
            }
            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("UserRepo", e.getMessage());
                e.printStackTrace();
            }
            @Override
            public void onComplete() {}
        };

        compositeDisposable.add(deleteUserObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(deleteUserObserver));
    }


    public MutableLiveData<User> loginUser(String email, String password) {
        MutableLiveData<User> loginResult = new MutableLiveData<>();

        // Fetch the current user list
        List<User> currentUserList = userList.getValue();
        if (currentUserList != null) {
            for (User user : currentUserList) {
                // Check if username and password match
                if (user.getEmail().equals(email) && user.getPassword().equals(password)) {
                    loginResult.postValue(user); // Set the logged-in user
                    return loginResult;
                }
            }
        }
        loginResult.postValue(null); // If no match, set null (indicating login failure)
        return loginResult;
    }

    public MutableLiveData<List<User>> getUsers(){
        return userList;
    }

    public void onCleared(){
        compositeDisposable.clear();
    }
}
