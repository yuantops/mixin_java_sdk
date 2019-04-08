package one.mixin.api.network;

import one.mixin.api.Config;
import one.mixin.api.network.model.Asset;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class MixinClientTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(MixinClient.class);

    MixinClient mixinClient = new MixinClient();

    @Test
    public void testReadAssets() {
        List<Asset> assets= mixinClient.readAssets();
        Assert.assertTrue(assets != null);
        Assert.assertTrue(assets.size() > 1);
        System.out.println(assets.get(0).getAssetId());
    }

    @Test
    public void testReadAssetById() {
        Asset asset= mixinClient.readAsset("43d61dcd-e413-450d-80b8-101d5e903357");
        Assert.assertTrue(StringUtils.isNotEmpty(asset.getChainId()));
        LOGGER.info("{}", asset);
    }

    @Test
    public void testVerifyPin() {
        Object result = mixinClient.verifyPin(Config.PIN, null, null, null);
        LOGGER.info("result:{}", result);

    }
}
