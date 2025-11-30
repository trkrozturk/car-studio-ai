package org.car.studio.fal;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;

@RegisterRestClient(configKey = "fal-api")
@Path("/fal-ai/nano-banana-pro/edit")
public interface FalClientV2 {

    @POST
    @ClientHeaderParam(name = "Authorization", value = "Key ${fal.key}")
    FalResponse edit(FalRequest request);
}

