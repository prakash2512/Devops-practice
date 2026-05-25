#!/bin/bash
sudo apt-get update
### Installing Apche2
#sudo apt install apache2 -y
### Installing Java 21
sudo apt install openjdk-21-jdk -y
### Installing Docker
sudo apt-get update
sudo apt-get install ca-certificates curl -y
sudo install -m 0755 -d /etc/apt/keyrings
sudo curl -fsSL https://download.docker.com/linux/ubuntu/gpg -o /etc/apt/keyrings/docker.asc
sudo chmod a+r /etc/apt/keyrings/docker.asc

echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.asc] https://download.docker.com/linux/ubuntu \
  $(. /etc/os-release && echo "${UBUNTU_CODENAME:-$VERSION_CODENAME}") stable" | \
  sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

sudo apt update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-buildx-plugin docker-compose-plugin
sudo systemctl enable docker
sudo systemctl start docker

# Detect Ubuntu Version
UBUNTU_VERSION=$(lsb_release -rs)

echo "Detected Ubuntu Version: $UBUNTU_VERSION"

# ============================================
# Ubuntu 22.04 Jenkins Setup
# ============================================
if [[ "$UBUNTU_VERSION" == "22.04" ]]; then

    echo "Configuring Jenkins for Ubuntu 22.04..."

    sudo mkdir -p /usr/share/keyrings

    sudo wget -O /usr/share/keyrings/jenkins-keyring.asc \
    https://pkg.jenkins.io/debian-stable/jenkins.io-2023.key

    echo "deb [signed-by=/usr/share/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | \
    sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null

# ============================================
# Ubuntu 24.04 Jenkins Setup
# ============================================
elif [[ "$UBUNTU_VERSION" == "24.04" ]]; then

    echo "Configuring Jenkins for Ubuntu 24.04..."

    sudo mkdir -p /etc/apt/keyrings

    sudo wget -O /etc/apt/keyrings/jenkins-keyring.asc \
    https://pkg.jenkins.io/debian-stable/jenkins.io-2026.key

    echo "deb [signed-by=/etc/apt/keyrings/jenkins-keyring.asc] https://pkg.jenkins.io/debian-stable binary/" | \
    sudo tee /etc/apt/sources.list.d/jenkins.list > /dev/null

else
    echo "Unsupported Ubuntu Version: $UBUNTU_VERSION"
    exit 1
fi

  
sudo apt-get update
sudo apt install jenkins -y
sudo systemctl enable jenkins
sudo systemctl start jenkins
sudo groupadd docker
sudo usermod -aG docker $USER
sudo usermod -aG docker jenkins
sudo systemctl restart docker
sudo systemctl restart jenkins
mkdir -p /var/backups/docker-logs/
chown -R jenkins:jenkins /var/backups/docker-logs
