package one.mixin.api.network;

import one.mixin.api.Config;
import one.mixin.api.network.model.Asset;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class MixinClientTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MixinClient.class);

    MixinClient mixinClient = new MixinClient();

    @Test
    public void testReadAssets() {
        List<Asset> assets= mixinClient.readAssets();
        Assert.assertTrue(assets != null);
        Assert.assertTrue(assets.size() > 1);
        System.out.println(assets.get(0).getAssetId());
        LOGGER.info("{}", assets);
    }

    @Test
    public void testReadAssetById() {
        String assetId = "965e5c6e-434c-3fa9-b780-c50f43cd955c";
        Asset asset= mixinClient.readAsset(assetId);
        Assert.assertTrue(StringUtils.isNotEmpty(asset.getChainId()));
        LOGGER.info("{}", asset);
    }

    @Test
    public void testVerifyPin() {
        Object result = mixinClient.verifyPin(Config.PIN, null, null, null);
        LOGGER.info("result:{}", result);
    }

    @Test
    public void testTransfer() {
        String assetId = "965e5c6e-434c-3fa9-b780-c50f43cd955c";
        String opponentId = "7b3f0a95-3ee9-4c1b-8ae9-170e3877d909";
        Object result = mixinClient.transfer(opponentId, assetId, "0.001","hello", UUID.randomUUID().toString());
        LOGGER.info("result:{}", result);
    }
}
