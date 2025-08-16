#!/bin/bash

# 定义变量
# 直接使用 gameServer.jar 的绝对路径
LOCAL_FILE="/Volumes/MyDisk/gamedevelopment/Server_game_split_700_61/gameServer/build/gameServer.jar"
REMOTE_USER="root"
REMOTE_IP="108.187.1.61"
REMOTE_PORT="40846" # SSH 端口
REMOTE_PATH="/opt/game_8802/Server/GameServer/bin/"
REMOTE_PASSWORD="67G3oW6b97RQ" # 请将此处的 <Your_Server_Password_Here> 替换为您的实际密码

# 检查本地文件是否存在
if [ ! -f "$LOCAL_FILE" ]; then
    echo "错误：本地文件 $LOCAL_FILE 不存在。"
    exit 1
fi

# 确保安装了 sshpass
echo "正在检查并安装 sshpass..."
# 检查是否安装了 sshpass
if ! command -v sshpass &> /dev/null
then
    echo "sshpass 未安装，正在尝试安装..."
    # 对于 macOS (使用 Homebrew)
    if [[ "$OSTYPE" == "darwin"* ]]; then
        brew install sshpass
    # 对于 Debian/Ubuntu
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        sudo apt-get update
        sudo apt-get install sshpass -y
    else
        echo "不支持的操作系统，请手动安装 sshpass。"
        exit 1
    fi
    if ! command -v sshpass &> /dev/null
    then
        echo "sshpass 安装失败，请手动安装后重试。"
        exit 1
    fi
    echo "sshpass 安装成功。"
fi


# 停止远程服务器上的服务（如果需要）
# 远程执行 SSH 命令，通过 -p 指定端口，并使用 sshpass 传递密码
echo "正在尝试停止远程服务器上的旧服务..."
sshpass -p "${REMOTE_PASSWORD}" ssh -p ${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_IP} "pkill -f 'java -jar gameServer.jar'"

# 上传文件
echo "正在上传 gameServer.jar 到服务器..."
# 使用 SCP 命令，通过 -P 指定端口（注意这里是大写 P），并使用 sshpass 传递密码
sshpass -p "${REMOTE_PASSWORD}" scp -P ${REMOTE_PORT} "$LOCAL_FILE" "${REMOTE_USER}@${REMOTE_IP}:${REMOTE_PATH}"

# 检查 SCP 是否成功
if [ $? -eq 0 ]; then
    echo "文件上传成功！"
    # 远程执行 SSH 命令以启动服务
    echo "正在远程启动服务..."
    # 使用用户提供的启动指令
    sshpass -p "${REMOTE_PASSWORD}" ssh -p ${REMOTE_PORT} ${REMOTE_USER}@${REMOTE_IP} "cd /opt/game_8802 && ./restart_namegameserver.sh"
    echo "服务启动命令已发送。"
else
    echo "错误：文件上传失败。"
    exit 1
fi
