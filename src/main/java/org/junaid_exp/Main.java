package org.junaid_exp;

import org.junaid_exp.utils.CVUtils;

public class Main {

    static {

    }

    static void main() {
        CVUtils cvUtils = new CVUtils();
//        cvUtils.playVideo("C:\\Vids\\sample.mp4");
        //cvUtils.playVideoWithLandmarks(0,800,800);
        cvUtils.playVideoWithLandmarks("C:\\Vids\\sample.mp4",600,800);

    }
}
