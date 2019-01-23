package com.kin.ecosystem.transfer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.File;

import static org.junit.Assert.assertEquals;

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner.class)
public class AccountInfoManagerTest{

    @Test
    public void testWriteRead() {
        AccountInfoManager accountInfoManager = new AccountInfoManager(Robolectric.setupActivity(TestActivity.class));
        String publicAddress = "myPublicAddress";
        final boolean init = accountInfoManager.init(publicAddress);
        assertEquals(init,true);
        final String dataWritten = accountInfoManager.readFile(accountInfoManager.getFileProviderFullPath());
        assertEquals(publicAddress, dataWritten);
    }
}
