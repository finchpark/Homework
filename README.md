## Vagrant Up

실행하고자 하는 디렉토리에서 cmd에 vagrant up을 입력하면 다음과 같이 실행됩니다.

	// HadoopProject 디렉토리에서 vagrant up을 실행했을 때 예시
	C:\HadoopProject>vagrant up
	Bringing machine 'master' up with 'virtualbox' provider...
	Bringing machine 'slave1' up with 'virtualbox' provider...
	Bringing machine 'slave2' up with 'virtualbox' provider...
	==> master: Importing base box 'ubuntu/trusty64'...
	==> master: Matching MAC address for NAT networking...
	==> master: Checking if box 'ubuntu/trusty64' is up to date...
	
	// 이하 생략

### Vagrantfile
Vagrantfile은 다음과 같이 작성되어 있습니다.
**Vagrantfile**
	
	# -*- mode: ruby -*-
	# vi: set ft=ruby :
	
	Vagrant.configure(2) do |config|	  	  
	  # Master node
	  config.vm.define "master" do |master|
		master.vm.provider "virtualbox" do |v|
		  v.name = "master"
		  v.memory = 4096
		  v.cpus = 1
		end
		master.vm.box = "ubuntu/trusty64"
		master.vm.hostname = "master"
		master.vm.network "private_network", ip: "192.168.200.2"
		master.vm.provision "shell", path: "./setup.sh"
	  end

	  # Slave1 node
	  config.vm.define "slave1" do |slave1|
		slave1.vm.provider "virtualbox" do |v|
		  v.name = "slave1"
		  v.memory = 2048
		  v.cpus = 1
		end
		slave1.vm.box = "ubuntu/trusty64"
		slave1.vm.hostname = "slave1"
		slave1.vm.network "private_network", ip: "192.168.200.10"
		slave1.vm.provision "shell", path: "./setup.sh"
	  end

	  config.vm.define "slave2" do |slave2|
		slave2.vm.provider "virtualbox" do |v|
		  v.name = "slave2"
		  v.memory = 2048
		  v.cpus = 1
		end
		slave2.vm.box = "ubuntu/trusty64"
		slave2.vm.hostname = "slave2"
		slave2.vm.network "private_network", ip: "192.168.200.11"
		slave2.vm.provision "shell", path: "./setup.sh"
	  end
	end

### Shell Script
또한 vagrant up 하는 동안 자동으로 필요한 부분을 설정하기 위한 Shell Script는 다음과 같습니다.
**setup.sh**

	#!/bin/bash

	# Variables
	tools=/home/hadoop/tools
	JH=/home/hadoop/tools/jdk
	HH=/home/hadoop/tools/hadoop

	# Install JDK
	add-apt-repository ppa:openjdk-r/ppa
	apt-get update
	apt-get install -y openjdk-8-jre-headless
	apt-get install -y openjdk-8-jdk

	# Install expect
	apt-get install -y expect

	# Install Git
	apt-get install -y git

	# Add Group and User
	addgroup hadoop
	useradd -g hadoop -d /home/hadoop/ -s /bin/bash -m hadoop
	echo -e "hadoop\nhadoop" | (passwd hadoop)

	# Make directory for hdfs
	host=`hostname`
	if [ $host == "master" ]; then
		mkdir -p /home/hadoop/hdfs/name
	else
		mkdir -p /home/hadoop/hdfs/data
	fi

	# Download Hadoop
	mkdir $tools
	cd $tools
	wget http://ftp.daum.net/apache//hadoop/common/hadoop-1.2.1/hadoop-1.2.1.tar.gz
	tar xvf hadoop-1.2.1.tar.gz
	ln -s $tools/hadoop-1.2.1 $tools/hadoop
	ln -s /usr/lib/jvm/java-1.8.0-openjdk-amd64 $tools/jdk

	# Download Maven
	cd $tools
	wget http://mirror.apache-kr.org/maven/maven-3/3.2.5/binaries/apache-maven-3.2.5-bin.tar.gz
	tar xvf apache-maven-3.2.5-bin.tar.gz
	ln -s $tools/apache-maven-3.2.5 $tools/maven

	# Hadoop Setting
	# hadoop-env.sh
	echo "export JAVA_HOME=/home/hadoop/tools/jdk" >> $HH/conf/hadoop-env.sh
	echo "export HADOOP_HOME=/home/hadoop/tools/hadoop" >> $HH/conf/hadoop-env
	echo "export HADOOP_HOME_WARN_SUPRESS=\"TRUE\"" >> $HH/conf/hadoop-env.sh
	echo "export HADOOP_OPTS=-server" >> $HH/conf/hadoop-env.sh

	# core-site.xml
	echo "<?xml version=\"1.0\"?>" > $HH/conf/core-site.xml
	echo "<?xml-stylesheet type=\"text/xsl\" href=\"configuration.xsl\"?>" >> $HH/conf/core-site.xml
	echo "" >> $HH/conf/core-site.xml
	echo "<!-- Put site-specific property overrides in this file. -->" >> $HH/conf/core-site.xml
	echo "" >> $HH/conf/core-site.xml
	echo "<configuration>" >> $HH/conf/core-site.xml
	echo "  <property>" >> $HH/conf/core-site.xml
	echo "    <name>fs.default.name</name>" >> $HH/conf/core-site.xml
	echo "    <value>hdfs://master:9000</value>" >> $HH/conf/core-site.xml
	echo "  </property>" >> $HH/conf/core-site.xml
	echo "</configuration>" >> $HH/conf/core-site.xml

	# hdfs-site.xml
	echo "<?xml version=\"1.0\"?>" > $HH/conf/hdfs-site.xml
	echo "<?xml-stylesheet type=\"text/xsl\" href=\"configuration.xsl\"?>" >> $HH/conf/hdfs-site.xml
	echo "" >> $HH/conf/hdfs-site.xml
	echo "<!-- Put site-specific property overrides in this file. -->" >> $HH/conf/hdfs-site.xml
	echo "" >> $HH/conf/hdfs-site.xml
	echo "<configuration>" >> $HH/conf/hdfs-site.xml
	echo "  <property>" >> $HH/conf/hdfs-site.xml
	echo "    <name>dfs.name.dir</name>" >> $HH/conf/hdfs-site.xml
	echo "    <value>/home/hadoop/hdfs/name</value>" >> $HH/conf/hdfs-site.xml
	echo "  </property>" >> $HH/conf/hdfs-site.xml
	echo "" >> $HH/conf/hdfs-site.xml
	echo "  <property>" >> $HH/conf/hdfs-site.xml
	echo "    <name>dfs.data.dir</name>" >> $HH/conf/hdfs-site.xml
	echo "    <value>/home/hadoop/hdfs/data</value>" >> $HH/conf/hdfs-site.xml
	echo "  </property>" >> $HH/conf/hdfs-site.xml
	echo "" >> $HH/conf/hdfs-site.xml
	echo "  <property>" >> $HH/conf/hdfs-site.xml
	echo "    <name>dfs.replication</name>" >> $HH/conf/hdfs-site.xml
	echo "    <value>3</value>" >> $HH/conf/hdfs-site.xml
	echo "  </property>" >> $HH/conf/hdfs-site.xml
	echo "</configuration>" >> $HH/conf/hdfs-site.xml

	# mapred-site.xml
	echo "<?xml version=\"1.0\"?>" > $HH/conf/mapred-site.xml
	echo "<?xml-stylesheet type=\"text/xsl\" href=\"configuration.xsl\"?>" >> $HH/conf/mapred-site.xml
	echo "" >> $HH/conf/mapred-site.xml
	echo "<!-- Put site-specific property overrides in this file. -->" >> $HH/conf/mapred-site.xml
	echo "" >> $HH/conf/mapred-site.xml
	echo "<configuration>" >> $HH/conf/mapred-site.xml
	echo "  <property>" >> $HH/conf/mapred-site.xml
	echo "    <name>mapred.job.tracker</name>" >> $HH/conf/mapred-site.xml
	echo "    <value>master:9001</value>" >> $HH/conf/mapred-site.xml
	echo "  </property>" >> $HH/conf/mapred-site.xml
	echo "</configuration>" >> $HH/conf/mapred-site.xml

	# masters, slaves
	echo "master" > $HH/conf/masters
	echo "slave1" > $HH/conf/slaves
	echo "slave2" >> $HH/conf/slaves
	# End Hadoop Setting

	# Setting Environment
	chown -R hadoop:hadoop /home/hadoop
	chmod 755 -R /home/hadoop
	echo "" >> ~hadoop/.bashrc
	echo "export JAVA_HOME=$JH" >> ~hadoop/.bashrc
	echo "export M2_HOME=$tools/maven" >> ~hadoop/.bashrc
	echo "export PATH=\$PATH:\$JAVA_HOME/bin:\$HH/bin" >> ~hadoop/.bashrc
	echo "export PATH=\$PATH:\$M2_HOME/bin" >> ~hadoop/.bashrc

	# Setting Hosts
	# /etc/hosts Setting
	echo "fe00::0 ip6-localnet" > /etc/hosts
	echo "ff00::0 ip6-mcastprefix" >> /etc/hosts
	echo "ff02::1 ip6-allnodes" >> /etc/hosts
	echo "ff02::2 ip6-allrouters" >> /etc/hosts
	echo "ff02::3 ip6-allhosts" >> /etc/hosts
	echo "192.168.200.2 master" >> /etc/hosts
	echo "192.168.200.10 slave1" >> /etc/hosts
	echo "192.168.200.11 slave2" >> /etc/hosts

## 접속
VM에 접속할 Host는 다음과 같습니다.

	master: 127.0.0.1:2222
	slave1: 127.0.0.1:2200
	slave2: 127.0.0.1:2201
	
* Port가 할당되는 순서는 master > slave1 > slave2의 순서입니다.
* 만일 해당 Port중 하나가 사용되고 있다면, 다른 Port로 할당 받습니다.

		할당받는 순서
		2222 > 2200 > 2201 > 2202 > 2203 ...

* 각 아이디와 비밀번호는 다음과 같습니다.

		ID: root    / vagrant / hadoop
		PW: vagrant / vagratn / hadoop

### Login

	vagrant@master:~$ su - hadoop
	Password:

위와 같이 입력하여 hadoop ID로 로그인 합니다.

### 암호화 키 생성
master와 slave1, 2간의 **안전한 통신을 위한 키를 생성**합니다.

	hadoop@master:~$ ssh-keygen -t rsa
**입력 받는 부분**에서는 모두 **엔터를 입력**하고 넘어 갑니다.

	hadoop@master:~$ ssh-keygen -t rsa
	Generating public/private rsa key pair.
	Enter file in which to save the key (/home/hadoop//.ssh/id_rsa):
	Created directory '/home/hadoop//.ssh'.
	Enter passphrase (empty for no passphrase):
	Enter same passphrase again:
	Your identification has been saved in /home/hadoop//.ssh/id_rsa.
	Your public key has been saved in /home/hadoop//.ssh/id_rsa.pub.
	The key fingerprint is:
	ee:f7:23:56:a8:3a:49:88:64:16:ec:4e:a7:5f:95:e2 hadoop@master
	The key's randomart image is:
	+--[ RSA 2048]----+
	| .               |
	|  o              |
	| . .     .       |
	|  * . . o        |
	| * + o oS  .     |
	|  + . E.  . .    |
	|   . o ... .     |
	|    . o.. + .    |
	|      .o.o o..   |
	+-----------------+

위와 같은 결과가 나오면 됩니다.

#### **공개키**를 **authorized_keys에 저장**합니다.

	hadoop@master:~$ cat ~/.ssh/id_rsa.pub >> ~/.ssh/authorized_keys

#### **공개키**를 **Slave1, 2에 각각 배포**합니다.

	hadoop@master:~$ ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop@slave1
	hadoop@master:~$ ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop@slave2

Are you sure you want to continue connecting (yes/no)? 부분에서는 **yes를 입력**합니다.

	hadoop@master:~$ ssh-copy-id -i ~/.ssh/id_rsa.pub hadoop@slave1
	The authenticity of host 'slave1 (192.168.200.10)' can't be established.
	ECDSA key fingerprint is f6:f8:bb:c0:2a:4c:c3:3c:d6:81:06:5f:3d:ed:d1:7e.
	Are you sure you want to continue connecting (yes/no)? yes
	/usr/bin/ssh-copy-id: INFO: attempting to log in with the new key(s), to filter out any that are already installed
	/usr/bin/ssh-copy-id: INFO: 1 key(s) remain to be installed -- if you are prompted now it is to install the new keys
	hadoop@slave1's password:
	
	Number of key(s) added: 1
	
	Now try logging into the machine, with:   "ssh 'hadoop@slave1'"
	and check to make sure that only the key(s) you wanted were added.

#### 접속 확인
다음과 같이 입력해 정상적으로 접속이 되는지 확인합니다.

	hadoop@master:~$ ssh hadoop@slave1
	Welcome to Ubuntu 14.04.1 LTS (GNU/Linux 3.13.0-44-generic x86_64)

	// 이하 생략
	
	hadoop@slave1:~$

위와 같이 출력되면 됩니다.

