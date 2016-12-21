package com.ddscanner.rest;

import android.support.annotation.NonNull;

import com.ddscanner.DDScannerApplication;
import com.ddscanner.entities.AchievmentsResponseEntity;
import com.ddscanner.entities.AddDiveSpotResponseEntity;
import com.ddscanner.entities.AddressComponent;
import com.ddscanner.entities.Comments;
import com.ddscanner.entities.DiveCenterProfile;
import com.ddscanner.entities.DiveCentersResponseEntity;
import com.ddscanner.entities.DiveSpotDetailsEntity;
import com.ddscanner.entities.DiveSpotPhoto;
import com.ddscanner.entities.DiveSpotPhotosResponseEntity;
import com.ddscanner.entities.DiveSpotShort;
import com.ddscanner.entities.DivespotsWrapper;
import com.ddscanner.entities.EditDiveSpotWrapper;
import com.ddscanner.entities.FiltersResponseEntity;
import com.ddscanner.entities.ForeignUserDislikesWrapper;
import com.ddscanner.entities.ForeignUserLikeWrapper;
import com.ddscanner.entities.GoogleMapsGeocodeResponseEntity;
import com.ddscanner.entities.ProfileResponseEntity;
import com.ddscanner.entities.RegisterResponse;
import com.ddscanner.entities.Sealife;
import com.ddscanner.entities.SealifeShort;
import com.ddscanner.entities.SignInType;
import com.ddscanner.entities.SignUpResponseEntity;
import com.ddscanner.entities.Translation;
import com.ddscanner.entities.User;
import com.ddscanner.entities.request.DeleteImageRequest;
import com.ddscanner.entities.request.DiveSpotsRequestMap;
import com.ddscanner.entities.request.IdentifyRequest;
import com.ddscanner.entities.request.RegisterRequest;
import com.ddscanner.entities.request.ReportRequest;
import com.ddscanner.entities.request.SignInRequest;
import com.ddscanner.entities.request.SignUpRequest;
import com.ddscanner.entities.request.ValidationRequest;
import com.ddscanner.utils.Constants;
import com.ddscanner.utils.Helpers;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;

public class DDScannerRestClient {

    protected Gson gson = new Gson();

    public void getCountryCode(String lat, String lng, final ResultListener<String> resultListener) {
        Call<ResponseBody> call = RestClient.getGoogleMapsApiService().getCountryName(lat + "," + lng);
        call.enqueue(new ResponseEntityCallback<String>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<String> resultListener, String responseString) throws JSONException {
                GoogleMapsGeocodeResponseEntity responseEntity = gson.fromJson(responseString, GoogleMapsGeocodeResponseEntity.class);
                if (responseEntity.getResults().size() > 0 && responseEntity.getResults().get(0).getAddressComponents() != null) {
                    for (AddressComponent addressComponent : responseEntity.getResults().get(0).getAddressComponents()) {
                        if (addressComponent != null && addressComponent.getShortName() != null && addressComponent.getShortName().length() == 2) {
                            resultListener.onSuccess(addressComponent.getShortName());
                            return;
                        }
                    }
                }
            }
        });
    }

    public void getDiveSpotDetails(String diveSpotId, @NonNull final ResultListener<DiveSpotDetailsEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotDetails(diveSpotId);
        call.enqueue(new ResponseEntityCallback<DiveSpotDetailsEntity>(gson, resultListener) {
            @Override
            void handleResponseString(DDScannerRestClient.ResultListener<DiveSpotDetailsEntity> resultListener, String responseString) {
                DiveSpotDetailsEntity diveSpotResponseEntity = gson.fromJson(responseString, DiveSpotDetailsEntity.class);
                resultListener.onSuccess(diveSpotResponseEntity);
            }
        });
    }

    public void postCheckOut(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postCheckout(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postAddDiveSpotToFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddToFavorites(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void deleteDiveSpotFromFavourites(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postRemoveFromFavorites(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getComments(String diveSpotId, @NonNull final ResultListener<Comments> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getComments(diveSpotId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<Comments>(gson, resultListener) {
            @Override
            void handleResponseString(DDScannerRestClient.ResultListener<Comments> resultListener, String responseString) {
                Comments comments = new Gson().fromJson(responseString, Comments.class);
                resultListener.onSuccess(comments);
            }
        });
    }

    public void getAddedDiveSpots(String userId, final ResultListener<DivespotsWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersAdded(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(divespotsWrapper);
            }
        });
    }

    public void getEditedDiveSpots(String userId, final ResultListener<DivespotsWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersEdited(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(divespotsWrapper);
            }
        });
    }

    public void getUsersCheckins(String userId, final ResultListener<DivespotsWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersCheckins(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper divespotsWrapper = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(divespotsWrapper);
            }
        });
    }

    public void getUserLikes(String userId, final ResultListener<ForeignUserLikeWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getForeignUserLikes(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<ForeignUserLikeWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ForeignUserLikeWrapper> resultListener, String responseString) {
                ForeignUserLikeWrapper foreignUserLikeWrapper = new Gson().fromJson(responseString, ForeignUserLikeWrapper.class);
                resultListener.onSuccess(foreignUserLikeWrapper);
            }
        });
    }

    public void getUserDislikes(String userId, final ResultListener<ForeignUserDislikesWrapper> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getForeignUserDislikes(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<ForeignUserDislikesWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ForeignUserDislikesWrapper> resultListener, String responseString) {
                ForeignUserDislikesWrapper foreignUserDislikesWrapper = new Gson().fromJson(responseString, ForeignUserDislikesWrapper.class);
                resultListener.onSuccess(foreignUserDislikesWrapper);
            }
        });
    }

    public void postReportImage(String imageName, String reportName, String reportDescription, final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().reportImage(getReportRequest(reportName, reportDescription, imageName));
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getUsersComments(String userId, final ResultListener<Comments> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserComments(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<Comments>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<Comments> resultListener, String responseString) throws JSONException {
                Comments comments = new Gson().fromJson(responseString, Comments.class);
                resultListener.onSuccess(comments);
            }
        });
    }

    public void postLeaveReview(RequestBody id, RequestBody comment, RequestBody rating, List<MultipartBody.Part> image, RequestBody token, RequestBody sn, final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addCommentToDiveSpot(id, comment, rating, image, token, sn);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void putEditComment(String commentId, RequestBody _method, RequestBody comment, RequestBody rating, List<MultipartBody.Part> images_new, List<MultipartBody.Part> images_del, RequestBody token, RequestBody sn, ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().updateComment(commentId, _method, comment, rating, images_new, images_del, token, sn);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postAddSealife(final ResultListener<Sealife> resultListener, MultipartBody.Part image, RequestBody... requestBodies) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addSealife(
                image, requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5],
                requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9], requestBodies[10]
                );
        call.enqueue(new ResponseEntityCallback<Sealife>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<Sealife> resultListener, String responseString) throws JSONException {
                JSONObject jsonObject = new JSONObject(responseString);
                responseString = jsonObject.getString(Constants.ADD_DIVE_SPOT_ACTIVITY_SEALIFE);
                Sealife sealife = new Gson().fromJson(responseString, Sealife.class);
                resultListener.onSuccess(sealife);
            }
        });
    }

    public void postValidateDiveSpot(String diveSpotId, boolean isValid, @NonNull final ResultListener<Void> resultListener) {
        ValidationRequest validationRequest = new ValidationRequest();
        validationRequest.setSocial(DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
        validationRequest.setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
        validationRequest.setAppId(FirebaseInstanceId.getInstance().getId());
        validationRequest.setpush(FirebaseInstanceId.getInstance().getToken());
        validationRequest.setValid(isValid);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().divespotValidation(diveSpotId, validationRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getReportTypes(@NonNull final ResultListener<FiltersResponseEntity> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new ResponseEntityCallback<FiltersResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<FiltersResponseEntity> resultListener, String responseString) {
                FiltersResponseEntity filtersResponseEntity = new Gson().fromJson(responseString, FiltersResponseEntity.class);
                resultListener.onSuccess(filtersResponseEntity);
            }
        });
    }

    public void deleteUserComment(String commentId, @NonNull final ResultListener<Void> resultListener) {
        Map<String, String> map = getUserQueryMapRequest();
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().deleteComment(commentId, map);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postSendReportToComment(String reportType, String reportDescription, String commentId, @NonNull final ResultListener<Void> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().reportComment(commentId, getReportRequest(reportType, reportDescription));
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postLikeReview(String commentId, @NonNull final ResultListener<Void> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().likeComment(commentId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postDislikeReview(String commentId, @NonNull final ResultListener<Void> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().dislikeComment(commentId, getRegisterRequest());
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postLogin(String appId, SignInType signInType, String token, @NonNull final ResultListener<RegisterResponse> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().login(generateRegisterRequest(appId, signInType.getName(), token));
        call.enqueue(new ResponseEntityCallback<RegisterResponse>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<RegisterResponse> resultListener, String responseString) {
                RegisterResponse registerResponse = new Gson().fromJson(responseString, RegisterResponse.class);
                resultListener.onSuccess(registerResponse);
            }
        });
    }

    public void getFilters(@NonNull final ResultListener<FiltersResponseEntity> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getFilters();
        call.enqueue(new ResponseEntityCallback<FiltersResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<FiltersResponseEntity> resultListener, String responseString) {
                FiltersResponseEntity filtersResponseEntity = new Gson().fromJson(responseString, FiltersResponseEntity.class);
                resultListener.onSuccess(filtersResponseEntity);
            }
        });
    }

    @Deprecated
    public void postAddDiveSpot(@NonNull final ResultListener<DiveSpotShort> resultListener, List<MultipartBody.Part> sealife, List<MultipartBody.Part> images, RequestBody... requestBodies) {
        if (requestBodies.length != 13) {
            throw new RuntimeException("RequestBody parameters count must be 13");
        }
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addDiveSpot(
                requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5],
                requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9],
                sealife, images, requestBodies[10], requestBodies[11], requestBodies[12]
        );
        call.enqueue(new ResponseEntityCallback<DiveSpotShort>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DiveSpotShort> resultListener, String responseString) throws JSONException {
                JSONObject jsonObject = new JSONObject(responseString);
                String diveSpotString = jsonObject.getString(Constants.ADD_DIVE_SPOT_ACTIVITY_DIVESPOT);
                DiveSpotShort diveSpotShort = new Gson().fromJson(diveSpotString, DiveSpotShort.class);
                resultListener.onSuccess(diveSpotShort);
            }
        });
    }

    public void postAddDiveSpot(ResultListener<String> resultListener, List<MultipartBody.Part> sealifes, List<MultipartBody.Part> iamges,List<MultipartBody.Part> maps, RequestBody... requestBodies ) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddDiveSpot(requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5], requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9], requestBodies[10], iamges, maps, sealifes);
        call.enqueue(new ResponseEntityCallback<String>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<String> resultListener, String responseString) throws JSONException {
                resultListener.onSuccess(responseString);
            }
        });
    }

    public void postUpdateDiveSpot(ResultListener<Void> resultListener, List<MultipartBody.Part> sealifes, List<MultipartBody.Part> newPhotos, List<MultipartBody.Part> deletedPhotos, List<MultipartBody.Part> new_maps, List<MultipartBody.Part> deleted_maps, RequestBody... requestBodies) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postUpdateDiveSpot(requestBodies[0], requestBodies[1], requestBodies[2], requestBodies[3], requestBodies[4], requestBodies[5], requestBodies[6], requestBodies[7], requestBodies[8], requestBodies[9], requestBodies[10], requestBodies[11], newPhotos, deletedPhotos, new_maps, deleted_maps, sealifes);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getUsersFavourites(String userId, @NonNull final ResultListener<DivespotsWrapper> resultListener) {
        final Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUsersFavorites(userId, getUserQueryMapRequest());
        call.enqueue(new ResponseEntityCallback<DivespotsWrapper>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DivespotsWrapper> resultListener, String responseString) {
                DivespotsWrapper filtersResponseEntity = new Gson().fromJson(responseString, DivespotsWrapper.class);
                resultListener.onSuccess(filtersResponseEntity);
            }
        });
    }

    public void getDiveSpotsByArea(DiveSpotsRequestMap diveSpotsRequestMap, ResultListener<List<DiveSpotShort>> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotsByFilter(diveSpotsRequestMap);
        call.enqueue(new ResponseEntityCallback<List<DiveSpotShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<List<DiveSpotShort>> resultListener, String responseString) {
                Type listType = new TypeToken<List<DiveSpotShort>>(){}.getType();
                List<DiveSpotShort> diveSpots = (List<DiveSpotShort>) gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void getDiveCenters(LatLng latLng, final ResultListener<DiveCentersResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveCenters(getDivecentersRequestmap(latLng));
        call.enqueue(new ResponseEntityCallback<DiveCentersResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DiveCentersResponseEntity> resultListener, String responseString) throws JSONException {
                DiveCentersResponseEntity diveCentersResponseEntity = new Gson().fromJson(responseString, DiveCentersResponseEntity.class);
                resultListener.onSuccess(diveCentersResponseEntity);
            }
        });
    }

    /*Methods using in API v2_0*/

    public void getDiveSpotLanguages(ResultListener<Map<String, String>> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDivespotLanguages();
        call.enqueue(new ResponseEntityCallback<Map<String, String>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<Map<String, String>> resultListener, String responseString) throws JSONException {
                Map<String, String> result = new HashMap<String, String>();
                result = gson.fromJson(responseString, result.getClass());
                resultListener.onSuccess(result);
            }
        });
    }

    public void getDivespotsByName(String query, ResultListener<ArrayList<DiveSpotShort>> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDivespotsByName(query);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotShort>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<List<DiveSpotShort>>(){}.getType();
                ArrayList<DiveSpotShort> diveSpots = gson.fromJson(responseString, listType);
                resultListener.onSuccess(diveSpots);
            }
        });
    }

    public void getSealifesByQuery(ResultListener<ArrayList<SealifeShort>> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSealifesByLimit(100000);
        call.enqueue(new ResponseEntityCallback<ArrayList<SealifeShort>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<SealifeShort>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<SealifeShort>>(){}.getType();
                ArrayList<SealifeShort> sealifeShorts = gson.fromJson(responseString, listType);
                resultListener.onSuccess(sealifeShorts);
            }
        });

    }

    public void postUserSignUp(String email, String password, String userType, String lat, String lng, ResultListener<SignUpResponseEntity> resultListener) {
        // TODO Implement name setting and remove hardcode
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().signUpUser(getSignUpRequest(email, password, "asdf", userType, lat, lng));
        call.enqueue(new ResponseEntityCallback<SignUpResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<SignUpResponseEntity> resultListener, String responseString) throws JSONException {
                SignUpResponseEntity signUpResponseEntity = new Gson().fromJson(responseString, SignUpResponseEntity.class);
                resultListener.onSuccess(signUpResponseEntity);
            }
        });
    }

    public void postUserLogin(String email, String password, String lat, String lng, SignInType signInType, String token, ResultListener<SignUpResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().loginUser(getSignInRequest(email, password, lat, lng, signInType, token));
        call.enqueue(new ResponseEntityCallback<SignUpResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<SignUpResponseEntity> resultListener, String responseString) throws JSONException {
                SignUpResponseEntity signUpResponseEntity = new Gson().fromJson(responseString, SignUpResponseEntity.class);
                resultListener.onSuccess(signUpResponseEntity);
            }
        });
    }

    public void getUserSelfInformation(final ResultListener<ProfileResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSelfProfileInformation();
        call.enqueue(new ResponseEntityCallback<ProfileResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ProfileResponseEntity> resultListener, String responseString) throws JSONException {
                ProfileResponseEntity user = new Gson().fromJson(responseString, ProfileResponseEntity.class);
                resultListener.onSuccess(user);
            }
        });
    }

    public void postAddDiveSpotToDiveCenter(String diveSpotId, ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postAddDiveSpotToDiveCenter(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postRemoveDiveSpotToDiveCenter(String diveSpotId, ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postRemoveDiveSpotToDiveCenter(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getUserProfileInformation(String id, final ResultListener<User> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserInformation(id);
        call.enqueue(new ResponseEntityCallback<User>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<User> resultListener, String responseString) throws JSONException {
                User user = new Gson().fromJson(responseString, User.class);
                resultListener.onSuccess(user);
            }
        });
    }

    public void getUserAchivements(final ResultListener<AchievmentsResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getUserAchievements();
        call.enqueue(new ResponseEntityCallback<AchievmentsResponseEntity>(gson, resultListener) {

            @Override
            void handleResponseString(ResultListener<AchievmentsResponseEntity> resultListener, String responseString) throws JSONException {
                AchievmentsResponseEntity achievmentsResponseEntity = new Gson().fromJson(responseString, AchievmentsResponseEntity.class);
                resultListener.onSuccess(achievmentsResponseEntity);
            }
        });
    }

    public void postApproveDiveSpot(String diveSpotId, boolean value, ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postApproveDiveSpot(diveSpotId, value);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotsTranslations(String diveSpotId, ResultListener<ArrayList<Translation>> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotsTranslations(diveSpotId);
        call.enqueue(new ResponseEntityCallback<ArrayList<Translation>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<Translation>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<Translation>>(){}.getType();
                ArrayList<Translation> translations = gson.fromJson(responseString, listType);
                resultListener.onSuccess(translations);
            }
        });
    }

    public void postCheckIn(String diveSpotId, @NonNull final ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postCheckin(diveSpotId);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postMapsToDiveSpot(String id, ArrayList<String> images, final ResultListener<List<String>> resultListener) {
        List<MultipartBody.Part> imagesToSend = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            File image = new File(images.get(i));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData(Constants.ADD_DIVE_SPOT_ACTIVITY_IMAGES_ARRAY,
                    image.getName(), requestFile);
            imagesToSend.add(part);
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addMapsToDiveSpot(RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), id), imagesToSend );
        call.enqueue(new ResponseEntityCallback<List<String>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<List<String>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<List<String>>(){}.getType();
                ArrayList<String> photos = gson.fromJson(responseString, listType);
                resultListener.onSuccess(photos);
            }
        });
    }

    public void postPhotosToDiveSpot(String id, ArrayList<String> images, final ResultListener<List<String>> resultListener) {
        List<MultipartBody.Part> imagesToSend = new ArrayList<>();
        for (int i = 0; i < images.size(); i++) {
            File image = new File(images.get(i));
            RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), image);
            MultipartBody.Part part = MultipartBody.Part.createFormData("images[]",
                    image.getName(), requestFile);
            imagesToSend.add(part);
        }
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().addPhotosToDiveSpot(RequestBody.create(MediaType.parse(Constants.MULTIPART_TYPE_TEXT), id), imagesToSend );
        call.enqueue(new ResponseEntityCallback<List<String>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<List<String>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<List<String>>(){}.getType();
                ArrayList<String> photos = gson.fromJson(responseString, listType);
                resultListener.onSuccess(photos);
            }
        });
    }

    public void getSealifeDetails(String id, ResultListener<Sealife> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getSealifeDetails(id);
        call.enqueue(new ResponseEntityCallback<Sealife>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<Sealife> resultListener, String responseString) throws JSONException {
                Sealife sealife = gson.fromJson(responseString, Sealife.class);
                resultListener.onSuccess(sealife);
            }
        });
    }

    public void getDiveSpotPhotos(String id, ResultListener<DiveSpotPhotosResponseEntity> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotPhotos(id);
        call.enqueue(new ResponseEntityCallback<DiveSpotPhotosResponseEntity>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<DiveSpotPhotosResponseEntity> resultListener, String responseString) throws JSONException {
                DiveSpotPhotosResponseEntity diveSpotPhotosResponseEntity = gson.fromJson(responseString, DiveSpotPhotosResponseEntity.class);
                resultListener.onSuccess(diveSpotPhotosResponseEntity);
            }
        });
    }

    public void postDeleteImage(String id, ResultListener<Void> resultListener) {
        DeleteImageRequest deleteImageRequest = new DeleteImageRequest(id);
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDeleteImage(deleteImageRequest);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postLikePhoto(String id, ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postLikePhoto(id);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void postDislikePhoto(String id, ResultListener<Void> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().postDislikePhoto(id);
        call.enqueue(new NoResponseEntityCallback(gson, resultListener));
    }

    public void getDiveSpotMaps(String id, ResultListener<ArrayList<DiveSpotPhoto>> resultListener) {
        Call<ResponseBody> call = RestClient.getDdscannerServiceInstance().getDiveSpotMaps(id);
        call.enqueue(new ResponseEntityCallback<ArrayList<DiveSpotPhoto>>(gson, resultListener) {
            @Override
            void handleResponseString(ResultListener<ArrayList<DiveSpotPhoto>> resultListener, String responseString) throws JSONException {
                Type listType = new TypeToken<ArrayList<DiveSpotPhoto>>(){}.getType();
                ArrayList<DiveSpotPhoto> photos = gson.fromJson(responseString, listType);
                resultListener.onSuccess(photos);
            }
        });
    }

    private ReportRequest getReportRequest(String reportType, String reportDescription) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setType(reportType);
        reportRequest.setDescription(reportDescription);
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken().isEmpty()) {
            reportRequest.setSocial(DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
            reportRequest.setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
        }
        return reportRequest;
    }

    private ReportRequest getReportRequest(String reportType, String reportDescription, String imageName) {
        ReportRequest reportRequest = new ReportRequest();
        reportRequest.setType(reportType);
        reportRequest.setDescription(reportDescription);
        reportRequest.setName(imageName);
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken().isEmpty()) {
            reportRequest.setSocial(DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
            reportRequest.setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
        }
        return reportRequest;
    }

    private Map<String, String> getDivecentersRequestmap(LatLng latLng) {
        Map<String, String> map = new HashMap<>();
        map.put("latLeft", String.valueOf(latLng.latitude - 2.0));
        map.put("lngLeft", String.valueOf(latLng.longitude - 2.0));
        map.put("lngRight", String.valueOf(latLng.longitude + 2.0));
        map.put("latRight", String.valueOf(latLng.latitude + 2.0));
        return map;
    }

    private Map<String, String> getUserQueryMapRequest() {
        Map<String, String> map = new HashMap<>();
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            map.put("social", DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
            map.put("token", DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn().equals("tw")) {
                map.put("secret", DDScannerApplication.getInstance().getSharedPreferenceHelper().getSecret());
            }
        } else {
            return new HashMap<>();
        }
        return map;
    }

    private RegisterRequest getRegisterRequest() {
        RegisterRequest registerRequest = new RegisterRequest();
        if (!DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            registerRequest.setAppId(FirebaseInstanceId.getInstance().getId());
            registerRequest.setpush(FirebaseInstanceId.getInstance().getToken());
            return registerRequest;
        }

        registerRequest.setSocial(DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
        registerRequest.setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn().equals("tw")) {
            registerRequest.setSecret(DDScannerApplication.getInstance().getSharedPreferenceHelper().getSecret());
        }
        registerRequest.setAppId(FirebaseInstanceId.getInstance().getId());
        registerRequest.setpush(FirebaseInstanceId.getInstance().getToken());
        return registerRequest;
    }

    private IdentifyRequest getUserIdentifyData(String lat, String lng) {
        IdentifyRequest identifyRequest = new IdentifyRequest();
        identifyRequest.setAppId(FirebaseInstanceId.getInstance().getId());
        if (DDScannerApplication.getInstance().getSharedPreferenceHelper().isUserLoggedIn()) {
            identifyRequest.setSocial(DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn());
            identifyRequest.setToken(DDScannerApplication.getInstance().getSharedPreferenceHelper().getToken());
            if (DDScannerApplication.getInstance().getSharedPreferenceHelper().getSn().equals("tw")) {
                identifyRequest.setSecret(DDScannerApplication.getInstance().getSharedPreferenceHelper().getSecret());
            }
        }
        identifyRequest.setpush(FirebaseInstanceId.getInstance().getToken());
        if (lat != null && lng != null) {
            identifyRequest.setLat(lat);
            identifyRequest.setLng(lng);
        }
        identifyRequest.setType("android");
        return identifyRequest;
    }

    private RegisterRequest generateRegisterRequest(String appId, String socialNetworkName, String token) {
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setAppId(appId);
        registerRequest.setSocial(socialNetworkName);
        registerRequest.setToken(token);
        return registerRequest;
    }

    private SignUpRequest getSignUpRequest(String email, String password, String name, String userType, String lat, String lng) {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail(email);
        signUpRequest.setPassword(password);
        signUpRequest.setName(name);
        signUpRequest.setUserType(Helpers.getUserType(userType));
        signUpRequest.setPush(FirebaseInstanceId.getInstance().getToken());
        signUpRequest.setApp_id(FirebaseInstanceId.getInstance().getId());
        signUpRequest.setLat(lat);
        signUpRequest.setLng(lng);
        signUpRequest.setDeviceType(2);
        return signUpRequest;
    }

    private SignInRequest getSignInRequest(String email, String password, String lat, String lng, SignInType signInType, String token) {
        SignInRequest signInRequest = new SignInRequest();
        signInRequest.setDeviceType(2);
        signInRequest.setEmail(email);
        signInRequest.setPassword(password);
        if (signInType != null) {
            switch (signInType) {
                case GOOGLE:
                    signInRequest.setProviderType(2);
                    break;
                case FACEBOOK:
                    signInRequest.setProviderType(1);
                    break;
                default:
                    signInRequest.setProviderType(null);
                    break;
            }
        }
        signInRequest.setToken(token);
        signInRequest.setPush(FirebaseInstanceId.getInstance().getToken());
        signInRequest.setApp_id(FirebaseInstanceId.getInstance().getId());
        signInRequest.setLat(lat);
        signInRequest.setLng(lng);
        return signInRequest;
    }

    public static abstract class ResultListener<T> {
        private boolean isCancelled;

        public abstract void onSuccess(T result);

        public abstract void onConnectionFailure();

        public abstract void onError(ErrorType errorType, Object errorData, String url, String errorMessage);

        public boolean isCancelled() {
            return isCancelled;
        }

        public void setCancelled(boolean cancelled) {
            isCancelled = cancelled;
        }
    }

    public enum ErrorType {
        BAD_REQUEST_ERROR_400, ENTITY_NOT_FOUND_404, RIGHTS_NOT_FOUND_403, UNAUTHORIZED_401, DIVE_SPOT_NOT_FOUND_ERROR_C802, COMMENT_NOT_FOUND_ERROR_C803, UNPROCESSABLE_ENTITY_ERROR_422, SERVER_INTERNAL_ERROR_500, IO_ERROR, JSON_SYNTAX_EXCEPTION, UNKNOWN_ERROR
    }
}
