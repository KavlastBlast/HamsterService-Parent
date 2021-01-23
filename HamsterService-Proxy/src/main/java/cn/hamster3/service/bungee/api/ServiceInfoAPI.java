package cn.hamster3.service.bungee.api;

import cn.hamster3.service.bungee.handler.ServiceConnection;
import cn.hamster3.service.common.data.ServicePlayerInfo;
import cn.hamster3.service.common.entity.ServiceSenderInfo;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.UUID;

/**
 * 服务信息API
 * <p>
 * 通过这个类的静态方法可以快速查询其他服务器的状态
 * <p>
 * 也可以获取已连接至服务器的全部玩家信息（即使玩家在其他服务器）
 */
@SuppressWarnings({"unused", "RedundantSuppression", "SpellCheckingInspection"})
public class ServiceInfoAPI {
    private static ServiceConnection connection;
    private static HashSet<ServicePlayerInfo> playerInfos;
    private static HashSet<ServiceSenderInfo> senderInfos;

    /**
     * 这个类不应该由你实例化
     * <p>
     * 无论什么时候都不要调用这个类的构造方法
     *
     * @param connection 连接对象
     */
    public ServiceInfoAPI(ServiceConnection connection) {
        if (ServiceInfoAPI.connection != null) {
            throw new IllegalStateException("不允许重复初始化 ServiceMessageAPI !");
        }
        ServiceInfoAPI.connection = connection;
        playerInfos = new HashSet<>();
        senderInfos = new HashSet<>();

    }

    /**
     * 获取玩家信息，当玩家不在线时返回null
     *
     * @param playerName 玩家ID
     * @return 玩家信息
     */
    // 不加Nullable注解是因为觉得有些时候确信玩家在线
    // 这个时候获取又会提示null判断，就很烦
    public static ServicePlayerInfo getPlayerInfo(@NotNull String playerName) {
        for (ServicePlayerInfo info : playerInfos) {
            if (info.getPlayerName().equalsIgnoreCase(playerName)) {
                return info;
            }
        }
        return null;
    }

    /**
     * 获取玩家信息，当玩家不在线时返回null
     *
     * @param uuid 玩家的UUID
     * @return 玩家信息
     */
    public static ServicePlayerInfo getPlayerInfo(@NotNull UUID uuid) {
        for (ServicePlayerInfo info : playerInfos) {
            if (info.getUuid().equals(uuid)) {
                return info;
            }
        }
        return null;
    }

    /**
     * 获取全部在线玩家的信息
     *
     * @return 玩家们的信息
     */
    public static HashSet<ServicePlayerInfo> getOnlinePlayers() {
        return new HashSet<>(playerInfos);
    }

    /**
     * 获取服务端信息
     *
     * @param senderName 服务端id
     * @return 服务端信息
     */
    public static ServiceSenderInfo getSenderInfo(String senderName) {
        for (ServiceSenderInfo info : senderInfos) {
            if (info.getName().equalsIgnoreCase(senderName)) {
                return info;
            }
        }
        return null;
    }

    /**
     * 获取所有连接至服务中心的信息
     *
     * @return 服务器信息
     */
    public static HashSet<ServiceSenderInfo> getSenderInfos() {
        return senderInfos;
    }

    /**
     * 获取当前服务器的名称
     *
     * @return 服务器id
     */
    public static String getServerName() {
        return connection.getInfo().getName();
    }

    /**
     * 获取当前服务器的别名
     *
     * @return 服务器别名
     */
    public static String getServerNickName() {
        return connection.getInfo().getNickName();
    }

    /**
     * 获取当前服务器的SenderInfo对象
     *
     * @return 当前服务器的发送者信息
     */
    public static ServiceSenderInfo getSelfInfo() {
        return connection.getInfo();
    }

    /**
     * （当玩家进入子服时）设置一条玩家信息
     *
     * @param playerInfo 玩家信息
     */
    public void loadPlayerInfo(ServicePlayerInfo playerInfo) {
        playerInfos.remove(playerInfo);
        playerInfos.add(playerInfo);
    }

    /**
     * （当玩家与BC断开时）移除一条玩家信息
     *
     * @param uuid 玩家UUID
     */
    public void removePlayerInfo(UUID uuid) {
        ServicePlayerInfo info = getPlayerInfo(uuid);
        playerInfos.remove(info);
    }

    /**
     * 加载一条服务器信息
     *
     * @param senderInfo 服务器信息
     */
    public void loadServerInfo(ServiceSenderInfo senderInfo) {
        senderInfos.remove(senderInfo);
        senderInfos.add(senderInfo);
    }

    /**
     * 删除一条服务器信息
     *
     * @param name 服务器名称
     */
    public void removeSenderInfo(String name) {
        ServiceSenderInfo info = getSenderInfo(name);
        senderInfos.remove(info);
    }

    /**
     * 重设所有玩家信息
     *
     * @param playerInfos 玩家们的信息
     */
    public void resetAllPlayerInfo(HashSet<ServicePlayerInfo> playerInfos) {
        ServiceInfoAPI.playerInfos = playerInfos;
    }

    /**
     * 重设所有服务器信息
     *
     * @param senderInfos 所有服务器的信息
     */
    public void resetAllServerInfo(HashSet<ServiceSenderInfo> senderInfos) {
        ServiceInfoAPI.senderInfos = senderInfos;
    }
}