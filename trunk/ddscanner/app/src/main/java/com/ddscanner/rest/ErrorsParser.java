package com.ddscanner.rest;

import com.ddscanner.entities.errors.Field;
import com.ddscanner.entities.errors.GeneralError;
import com.ddscanner.entities.errors.BadRequestException;
import com.ddscanner.entities.errors.NotFoundException;
import com.ddscanner.entities.errors.ServerInternalErrorException;
import com.ddscanner.entities.errors.UnknownErrorException;
import com.ddscanner.entities.errors.ValidationError;
import com.ddscanner.entities.errors.ValidationErrorException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.Map;
import java.util.Set;

public class ErrorsParser {

    public static void checkForError(int responseCode, String json) throws BadRequestException, ValidationErrorException, ServerInternalErrorException, NotFoundException, UnknownErrorException {
        if (responseCode == 200) {
            return;
        }
        Gson gson = new Gson();
        GeneralError generalError;
        ValidationError validationError;
        switch (responseCode) {
            case 400:
                // bad request. for example event already happened or event preconditions are not held
                generalError = gson.fromJson(json, GeneralError.class);
                throw new BadRequestException().setGeneralError(generalError);
            case 404:
                // entity not found
                generalError = gson.fromJson(json, GeneralError.class);
                throw new NotFoundException().setGeneralError(generalError);
            case 422:
                // validation error
                validationError = new ValidationError();
                JsonElement jsonElement = new JsonParser().parse(json);
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
                Field field;
                for (Map.Entry<String, JsonElement> entry : entrySet) {
                    field = new Field();
                    field.setName(entry.getKey());
                    JsonArray jsonArray = entry.getValue().getAsJsonArray();
                    for (int i = 0; i < jsonArray.size(); i++) {
                        field.addError(jsonArray.get(i).getAsString());
                    }
                    validationError.addField(field);
                }
                throw new ValidationErrorException().setValidationError(validationError);
            case 500:
                // unknown server error
                generalError = gson.fromJson(json, GeneralError.class);
                throw new ServerInternalErrorException().setGeneralError(generalError);
            default:
                // If unexpected error code is received
                generalError = gson.fromJson(json, GeneralError.class);
                throw new UnknownErrorException().setGeneralError(generalError);
        }
    }
}
