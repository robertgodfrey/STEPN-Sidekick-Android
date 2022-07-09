package stepn.sidekick.stepnsidekick;

import static stepn.sidekick.stepnsidekick.Finals.*;

/**
 * Gem class for saving gem slot type, rarity, and gem mounted.
 *
 * @author Bob Godfrey
 * @version 1.3.0
 */

public class Gem {
    private int socketType, socketRarity, mountedGem, socketImageSource, gemImageSource;

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
                socketImageSource = R.drawable.gem_socket_gray;
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
}
