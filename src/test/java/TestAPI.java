/*
 * PACKAGE_NAME
 * =============================================
 * This File was Created by FrameDev
 * Please do not change anything without my consent!
 * =============================================
 * This Class was created at 25.01.2025 13:54
 */

import ch.framedev.simplejavautils.SimpleJavaUtils;
import org.junit.Assert;
import org.junit.Test;

public class TestAPI {

    @Test
    public void onTestSimpleJavaUtils() {
        Assert.assertTrue(new SimpleJavaUtils().isOnline("framedev.ch", 443));
    }
}
