package de.oliver.fancynpcs.skins;

import de.oliver.fancynpcs.api.skins.SkinData;
import org.mineskin.request.GenerateRequest;

public class SkinGenerationRequest {

    private final String id;
    private final SkinData.SkinVariant variant;
    private final GenerateRequest mineskinRequest;

    public SkinGenerationRequest(String id, SkinData.SkinVariant variant, GenerateRequest mineskinRequest) {
        this.id = id;
        this.variant = variant;
        this.mineskinRequest = mineskinRequest;
    }

    public SkinGenerationRequest(String id, SkinData.SkinVariant variant) {
        this.id = id;
        this.variant = variant;
        this.mineskinRequest = null;
    }

    public String getID() {
        return id;
    }

    public SkinData.SkinVariant getVariant() {
        return variant;
    }

    public GenerateRequest getMineskinRequest() {
        return mineskinRequest;
    }
}
