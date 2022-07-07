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

    public Gem(int socketType, int socketRarity, int mountedGem, int socketImageSource, int gemImageSource) {
        this.socketType = socketType;
        this.socketRarity = socketRarity;
        this.mountedGem = mountedGem;
        this.socketImageSource = socketImageSource;
        this.gemImageSource = gemImageSource;
    }

    public int getSocketType() {
        return socketType;
    }

    public void setSocketType(int socketType) {
        this.socketType = socketType;
        updateSocketResource();
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

    }

    public int socketImageSource() {
        return socketImageSource;
    }

    public int getGemImageSource() {
        return gemImageSource;
    }

    private void updateSocketResource() {
        switch (socketType) {
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
        }
    }

}
