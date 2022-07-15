package stepn.sidekick.stepnsidekick;

import static stepn.sidekick.stepnsidekick.Finals.*;

/**
 * Gem class for creating gem object
 *
 * @author Bob Godfrey
 * @version 1.3.0
 */

public class Gem {
    private int socketType, socketRarity, mountedGem, socketImageSource, gemImageSource;
    private float basePoints;

    public Gem(int socketType, int socketRarity, int mountedGem) {
        this.socketType = socketType;
        this.socketRarity = socketRarity;
        this.mountedGem = mountedGem;
        updateSocketResource();
        updateGemResource();
    }

    public int getSocketType() {
        return socketType;
    }

    public void setSocketType(int socketType) {
        this.socketType = socketType;
        updateSocketResource();
        updateGemResource();
    }

    public int getSocketRarity() {
        return socketRarity;
    }

    public void setSocketRarity(int socketRarity) {
        this.socketRarity = socketRarity;
        updateSocketResource();
    }

    public int getMountedGem() {
        return mountedGem;
    }

    public void setMountedGem(int mountedGem) {
        this.mountedGem = mountedGem;
        updateGemResource();
    }

    public int getSocketImageSource() {
        return socketImageSource;
    }

    public int getGemImageSource() {
        return gemImageSource;
    }

    public float getBasePoints() {
        return basePoints;
    }

    public void setBasePoints(float basePoints) {
        this.basePoints = basePoints;
    }

    public double getGemParams() {
        double gemParams;

        switch (mountedGem) {
            case 1:
                gemParams = 2 + (Math.floor(0.05 * basePoints * 10.0) / 10.0);
                break;
            case 2:
                gemParams = 8 + Math.floor(0.7 * basePoints * 10.0) / 10.0;
                break;
            case 3:
                gemParams = 25 + Math.floor(2.2 * basePoints * 10.0) / 10.0;
                break;
            case 4:
                gemParams = 72 + Math.floor(6 * basePoints * 10.0) / 10.0;
                break;
            case 5:
                gemParams = 200 + Math.floor(14 * basePoints * 10.0) / 10.0;
                break;
            case 6:
                gemParams = 400 + Math.floor(43 * basePoints * 10.0) / 10.0;
                break;
            default:
                gemParams = 0;
        }

        return gemParams;
    }

    public double getSocketParams() {
        double socketParams;

        switch (socketRarity) {
            case 1:
                socketParams = 1.1;
                break;
            case 2:
                socketParams = 1.2;
                break;
            case 3:
                socketParams = 1.3;
                break;
            case 4:
                socketParams = 1.5;
                break;
            default:
                socketParams = 1;
        }

        return socketParams;
    }

    public String getGemParamsString() {
        return "+ " + getGemParams();
    }

    public String getSocketParamsString() {
        return "× " + getSocketParams();
    }

    public String getTotalPointsString() {
        return "+ " + (Math.round(getGemParams() * getSocketParams() * 10) / 10.0);
    }

    public float getTotalPoints() {
        return (float) (Math.round(getGemParams() * getSocketParams() * 10) / 10.0);
    }

    private void updateSocketResource() {
        switch (socketType) {
            case EFF:
                switch (socketRarity) {
                    case 1:
                        socketImageSource = R.drawable.gem_socket_eff_1;
                        break;
                    case 2:
                        socketImageSource = R.drawable.gem_socket_eff_2;
                        break;
                    case 3:
                        socketImageSource = R.drawable.gem_socket_eff_3;
                        break;
                    case 4:
                        socketImageSource = R.drawable.gem_socket_eff_4;
                        break;
                    default:
                        socketImageSource = R.drawable.gem_socket_eff_0;
                        break;
                }
                break;
            case LUCK:
                switch (socketRarity) {
                    case 1:
                        socketImageSource = R.drawable.gem_socket_luck_1;
                        break;
                    case 2:
                        socketImageSource = R.drawable.gem_socket_luck_2;
                        break;
                    case 3:
                        socketImageSource = R.drawable.gem_socket_luck_3;
                        break;
                    case 4:
                        socketImageSource = R.drawable.gem_socket_luck_4;
                        break;
                    default:
                        socketImageSource = R.drawable.gem_socket_luck_0;
                        break;
                }
                break;
            case COMF:
                switch (socketRarity) {
                    case 1:
                        socketImageSource = R.drawable.gem_socket_comf_1;
                        break;
                    case 2:
                        socketImageSource = R.drawable.gem_socket_comf_2;
                        break;
                    case 3:
                        socketImageSource = R.drawable.gem_socket_comf_3;
                        break;
                    case 4:
                        socketImageSource = R.drawable.gem_socket_comf_4;
                        break;
                    default:
                        socketImageSource = R.drawable.gem_socket_comf_0;
                        break;
                }
                break;
            case RES:
                switch (socketRarity) {
                    case 1:
                        socketImageSource = R.drawable.gem_socket_res_1;
                        break;
                    case 2:
                        socketImageSource = R.drawable.gem_socket_res_2;
                        break;
                    case 3:
                        socketImageSource = R.drawable.gem_socket_res_3;
                        break;
                    case 4:
                        socketImageSource = R.drawable.gem_socket_res_4;
                        break;
                    default:
                        socketImageSource = R.drawable.gem_socket_res_0;
                        break;
                }
                break;
            default:
                switch (socketRarity) {
                    case 1:
                        socketImageSource = R.drawable.gem_socket_gray_1;
                        break;
                    case 2:
                        socketImageSource = R.drawable.gem_socket_gray_2;
                        break;
                    case 3:
                        socketImageSource = R.drawable.gem_socket_gray_3;
                        break;
                    case 4:
                        socketImageSource = R.drawable.gem_socket_gray_4;
                        break;
                    default:
                        socketImageSource = R.drawable.gem_socket_gray_0;
                        break;
                }
        }
    }

    private void updateGemResource() {
        switch (socketType) {
            case EFF:
                switch (mountedGem) {
                    case 1:
                        gemImageSource = R.drawable.gem_eff_level1;
                        break;
                    case 2:
                        gemImageSource = R.drawable.gem_eff_level2;
                        break;
                    case 3:
                        gemImageSource = R.drawable.gem_eff_level3;
                        break;
                    case 4:
                        gemImageSource = R.drawable.gem_eff_level4;
                        break;
                    case 5:
                        gemImageSource = R.drawable.gem_eff_level5;
                        break;
                    case 6:
                        gemImageSource = R.drawable.gem_eff_level6;
                        break;
                    default:
                        gemImageSource = R.drawable.gem_socket_plus;
                }
                break;
            case LUCK:
                switch (mountedGem) {
                    case 1:
                        gemImageSource = R.drawable.gem_luck_level1;
                        break;
                    case 2:
                        gemImageSource = R.drawable.gem_luck_level2;
                        break;
                    case 3:
                        gemImageSource = R.drawable.gem_luck_level3;
                        break;
                    case 4:
                        gemImageSource = R.drawable.gem_luck_level4;
                        break;
                    case 5:
                        gemImageSource = R.drawable.gem_luck_level5;
                        break;
                    case 6:
                        gemImageSource = R.drawable.gem_luck_level6;
                        break;
                    default:
                        gemImageSource = R.drawable.gem_socket_plus;
                }
                break;
            case COMF:
                switch (mountedGem) {
                    case 1:
                        gemImageSource = R.drawable.gem_comf_level1;
                        break;
                    case 2:
                        gemImageSource = R.drawable.gem_comf_level2;
                        break;
                    case 3:
                        gemImageSource = R.drawable.gem_comf_level3;
                        break;
                    case 4:
                        gemImageSource = R.drawable.gem_comf_level4;
                        break;
                    case 5:
                        gemImageSource = R.drawable.gem_comf_level5;
                        break;
                    case 6:
                        gemImageSource = R.drawable.gem_comf_level6;
                        break;
                    default:
                        gemImageSource = R.drawable.gem_socket_plus;
                }
                break;
            case RES:
                switch (mountedGem) {
                    case 1:
                        gemImageSource = R.drawable.gem_res_level1;
                        break;
                    case 2:
                        gemImageSource = R.drawable.gem_res_level2;
                        break;
                    case 3:
                        gemImageSource = R.drawable.gem_res_level3;
                        break;
                    case 4:
                        gemImageSource = R.drawable.gem_res_level4;
                        break;
                    case 5:
                        gemImageSource = R.drawable.gem_res_level5;
                        break;
                    case 6:
                        gemImageSource = R.drawable.gem_res_level6;
                        break;
                    default:
                        gemImageSource = R.drawable.gem_socket_plus;
                }
                break;
            default:
                gemImageSource = R.drawable.gem_socket_plus;

        }
    }

    // each gem image has different dimensions so need to manually set the padding for each one
    // i think this is easier than going in and editing all the vector files so they are the same size ¯\_(ツ)_/¯
    public int getTopPadding() {
        int topPadding;

        switch (mountedGem) {
            case 0:
                topPadding = 2;
                break;
            case 1:
                topPadding = 4;
                break;
            case 2:
            case 4:
            case 5:
                topPadding = 1;
                break;
            default:
                topPadding = 0;
                break;
        }

        return topPadding;
    }

    public int getBottomPadding() {
        int bottomPadding;

        switch (mountedGem) {
            case 0:
                bottomPadding = 2;
                break;
            case 1:
                bottomPadding = 3;
                break;
            case 2:
            case 3:
                bottomPadding = 1;
                break;
            default:
                bottomPadding = 0;
                break;
        }
        return bottomPadding;
    }
}
