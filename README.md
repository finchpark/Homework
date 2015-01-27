## **Vagrant Up**

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

### **Vagrantfile**
Vagrantfile은 다음과 같이 작성되어 있습니다.
**Vagrantfile**
	
	# -*- mode: ruby -*-
	# vi: set ft=ruby :
	
	# All Vagrant configuration is done below. The "2" in Vagrant.configure
	# configures the configuration version (we support older styles for
	# backwards compatibility). Please don't change it unless you know what
	# you're doing.
	Vagrant.configure(2) do |config|
	  # The most common configuration options are documented and commented below.
	  # For a complete reference, please see the online documentation at
	  # https://docs.vagrantup.com.
	
	  # Every Vagrant development environment requires a box. You can search for
	  # boxes at https://atlas.hashicorp.com/search.
	  
	  # master node
	  config.vm.define "master" do |master|
	    master.vm.provider "virtualbox" do |v|
	      v.name = "master"
	      v.memory = 4096
	      v.cpus = 1
	    end
	    master.vm.box = "ubuntu/trusty64"
	    master.vm.hostname = "master"
	    master.vm.network "private_network", ip: "192.168.200.2"
		master.vm.network "forwarded_port", host:50070, guest:50070
	    master.vm.network "forwarded_port", host:50030, guest:50030
	    master.vm.provision "shell", path: "./setup.sh"
	  end
	
	  # slave1 node
	  config.vm.define "slave1" do |slave1|
	    slave1.vm.provider "virtualbox" do |v|
	      v.name = "slave1"
	      v.memory = 2048
	      v.cpus = 1
	    end
	    slave1.vm.box = "ubuntu/trusty64"
	    slave1.vm.hostname = "slave1"
	    slave1.vm.network "private_network", ip: "192.168.200.10"
		slave1.vm.network "forwarded_port", host:50075, guest:50075
	    slave1.vm.network "forwarded_port", host:50035, guest:50035
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
		slave2.vm.network "forwarded_port", host:50076, guest:50075
	    slave2.vm.network "forwarded_port", host:50036, guest:50035
	    slave2.vm.provision "shell", path: "./setup.sh"
	  end
	
	  # Disable automatic box update checking. If you disable this, then
	  # boxes will only be checked for updates when the user runs
	  # `vagrant box outdated`. This is not recommended.
	  # config.vm.box_check_update = false
	
	  # Create a forwarded port mapping which allows access to a specific port
	  # within the machine from a port on the host machine. In the example below,
	  # accessing "localhost:8080" will access port 80 on the guest machine.
	  # config.vm.network "forwarded_port", guest: 80, host: 8080
	
	  # Create a private network, which allows host-only access to the machine
	  # using a specific IP.
	  # config.vm.network "private_network", ip: "192.168.33.10"
	
	  # Create a public network, which generally matched to bridged network.
	  # Bridged networks make the machine appear as another physical device on
	  # your network.
	  # config.vm.network "public_network"
	
	  # Share an additional folder to the guest VM. The first argument is
	  # the path on the host to the actual folder. The second argument is
	  # the path on the guest to mount the folder. And the optional third
	  # argument is a set of non-required options.
	  # config.vm.synced_folder "../data", "/vagrant_data"
	
	  # Provider-specific configuration so you can fine-tune various
	  # backing providers for Vagrant. These expose provider-specific options.
	  # Example for VirtualBox:
	  #
	  # config.vm.provider "virtualbox" do |vb|
	  #   # Display the VirtualBox GUI when booting the machine
	  #   vb.gui = true
	  #
	  #   # Customize the amount of memory on the VM:
	  #   vb.memory = "1024"
	  # end
	  #
	  # View the documentation for the provider you are using for more
	  # information on available options.
	
	  # Define a Vagrant Push strategy for pushing to Atlas. Other push strategies
	  # such as FTP and Heroku are also available. See the documentation at
	  # https://docs.vagrantup.com/v2/push/atlas.html for more information.
	  # config.push.define "atlas" do |push|
	  #   push.app = "YOUR_ATLAS_USERNAME/YOUR_APPLICATION_NAME"
	  # end
	
	  # Enable provisioning with a shell script. Additional provisioners such as
	  # Puppet, Chef, Ansible, Salt, and Docker are also available. Please see the
	  # documentation for more information about their specific syntax and use.
	  # config.vm.provision "shell", inline: <<-SHELL
	  #   sudo apt-get update
	  #   sudo apt-get install -y apache2
	  # SHELL
	end

### **Shell Script**
또한 vagrant up 하는 동안 자동으로 필요한 부분을 설정하기 위한 Shell Script는 다음과 같습니다.
**setup.sh**

	#!/bin/bash

	# Variables
	tools=/home/hadoop/tools
	JH=/home/hadoop/tools/jdk
	HH=/home/hadoop/tools/hadoop
	
	# Install JDK
	apt-get install -y openjdk-7-jre-headless
	apt-get install -y openjdk-7-jdk
	
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
	ln -s /usr/lib/jvm/java-1.7.0-openjdk-amd64 $tools/jdk
	
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
	echo "export HADOOP_HOME=/home/hadoop/tools/hadoop" >> ~hadoop/.bashrc
	echo "export PATH=\$PATH:\$JAVA_HOME/bin:\$HH/bin" >> ~hadoop/.bashrc
	echo "export PATH=\$PATH:\$M2_HOME/bin" >> ~hadoop/.bashrc
	echo "export PATH=\$PATH:\$HADOOP_HOME/bin" >> ~hadoop/.bashrc
	
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

## **접속**
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

### **Login**
Hadoop 계정으로 로그인 했다면 상관 없지만, 다른 계정으로 접속한 경우 다음과 같이 입력합니다.

	vagrant@master:~$ su - hadoop
	Password:

위와 같이 입력하여 hadoop ID로 로그인 합니다.

### **암호화 키 생성**
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

#### **접속 확인**
다음과 같이 입력해 정상적으로 접속이 되는지 확인합니다.

	hadoop@master:~$ ssh hadoop@slave1
	Welcome to Ubuntu 14.04.1 LTS (GNU/Linux 3.13.0-44-generic x86_64)

	// 이하 생략
	
	hadoop@slave1:~$

위와 같이 출력되면 됩니다.

위와 같이 출력되면 됩니다.

## **프로그램 실행**
### **Maven 디렉토리 생성하기**
프로젝트를 수행할 홈 디렉토리를 생성합니다.
		
	hadoop@master:~$ mvn archetype:generate

입력하시면 다음과 같은 입력을 요하는 부분이 나옵니다. 괄호 안()과 같이 입력합니다.
**(참고: Enter는 Enter키를 치고 넘어가라는 의미입니다.)**

	Choose a number or apply filter (format: [groupId:]artifactId, case sensitive contains): 522: (Enter)
	Choose org.apache.maven.archetypes:maven-archetype-quickstart version:
	1: 1.0-alpha-1
	2: 1.0-alpha-2
	3: 1.0-alpha-3
	4: 1.0-alpha-4
	5: 1.0
	6: 1.1
	Choose a number: 6: (Enter)

다음 입력 받는 부분이 나옵니다. 여기서는 GroudID와 ArtifactID를 정의해 줍니다. 역시 괄호() 안과 같이 입력해 주시면 됩니다.
**(참고: Enter는 Enter키를 치고 넘어가라는 의미입니다.)**

	Define value for property 'groupId': : HadoopHW
	Define value for property 'artifactId': : HadoopHW
	Define value for property 'version':  1.0-SNAPSHOT: :
	Define value for property 'package':  HadoopHW: :
	Confirm properties configuration:
	groupId: (HadoopHW)
	artifactId: (HadoopHW)
	version: 1.0-SNAPSHOT
	package: HadoopHW (Enter)
	 Y: : (Enter)

ls를 입력해 보시면, HadoopHW 디렉토리가 생성된 것을 확인할 수 있습니다.
HadoopHW 디렉토리로 이동합니다.

	hadoop@master:~$ cd HadoopHW

### **원격 저장소에서 소스 받아오기**
HadoopHW 폴더에서 git init을 해 줍니다.

	hadoop@master:/home/hadoop/HadoopHW$ git init
	Initialized empty Git repository in /home/hadoop/HadoopHW/.git/

이제 Git에게 원격 저장소의 주소를 알려줍니다.

	hadoop@master:/home/hadoop/HadoopHW$ git remote add -t \* -f origin https://github.com/finchpark/Homework.git
	Updating origin
	remote: Counting objects: 191, done.
	(이하 생략)

Maven 생성시 생성된 pom.xml과 src/ 폴더를 삭제합니다.

	hadoop@master:/home/hadoop/HadoopHW$ rm pom.xml
	hadoop@master:/home/hadoop/HadoopHW$ rm -r src

이제 원격 저장소의 내용을 받아 옵니다.

	hadoop@master:/home/hadoop/HadoopHW$ git pull origin master
	From https://github.com/finchpark/Homework
	* branch            master     -> FETCH_HEAD

**(참고)**
만일 받아오지 못하고 fatal: unable to access라는 에러가 뜬다면

	$ su
	$ vi /etc/resolv.conf

를 열어서 nameserver를 168.126.63.1로 입력해 DNS에서 주소를 받아올 수 있게 해 줘야 합니다.

	nameserver 168.126.63.1

### **Maven Package 생성 (jar 파일 생성) 하기**
이제 패키지를 생성합니다.

	hadoop@master:/home/hadoop/HadoopHW$ mvn package
	[INFO] Scanning for projects...
	[INFO]
	[INFO] ------------------------------------------------------------------------
	[INFO] Building HadoopHW 0.0.4-SNAPSHOT
	[INFO] ------------------------------------------------------------------------
	Downloading: https://repo.maven.apache.org/maven2/org/apache/maven/plugins/maven-resources-plugin/2.6/maven-resources-plugin-2.6.pom
	
	(이하생략)
	
	[INFO] ------------------------------------------------------------------------
	[INFO] BUILD SUCCESS
	[INFO] ------------------------------------------------------------------------
	[INFO] Total time: 03:43 min
	[INFO] Finished at: 2015-01-27T05:33:46+00:00
	[INFO] Final Memory: 20M/59M
	[INFO] ------------------------------------------------------------------------

### **jar 파일 실행 준비하기**
압축된 shakespeare.tar.gz 파일을 압축해제 합니다.

	hadoop@master:/home/hadoop/HadoopHW/files$ tar xvf shakespeare.tar.gz
	shakespeare/
	shakespeare/comedies
	shakespeare/glossary
	shakespeare/histories
	shakespeare/poems
	shakespeare/tragedies

이제 namenode를 포멧합니다. 재 포멧할 것인지 묻는 질문이 나온다면 Y(반드시 대문자로)를 입력해 줍니다.

	hadoop@master:/home/hadoop/HadoopHW$ hadoop namenode -format
	Re-format filesystem in /home/hadoop/hdfs/name ? (Y or N) Y

NameNode, JobTracker, SecondaryNamenode, DataNode를 실행시키기 위해 start-all.sh를 실행합니다.

	hadoop@master:/home/hadoop/HadoopHW$ start-all.sh

이후, jps를 입력했을 때 다음과 같이 나오면 정상적으로 시작된 것입니다.

	master
	15694 NameNode
	15951 JobTracker
	16050 Jps
	15876 SecondaryNameNode

	slave
	15201 TaskTracker
	15292 Jps
	15086 DataNode

이제, 확인해 볼 파일을 이동시킵니다.

	hadoop@master:/home/hadoop/HadoopHW$ hadoop dfs -put files/shakespeare /shakespeare

### **실행**
#### **WordFrequencyHomeWork Class 실행**

실행을 위해 다음과 같이 입력합니다.
hadoop jar [jar 파일 경로] [분석할 문서 경로] [결과를 받을 경로]의 순서입니다.

	hadoop@master:/home/hadoop/HadoopHW$ hadoop jar target/HadoopHW-0.0.4-SNAPSHOT-jar-with-dependencies.jar HadoopHW.WordFrequenceHomeWork /shakespeare /wordFrequency
	
다음과 같은 메시지가 뜨면서 진행됩니다.

	15/01/27 05:45:31 INFO input.FileInputFormat: Total input paths to process : 5
	15/01/27 05:45:31 INFO util.NativeCodeLoader: Loaded the native-hadoop library
	15/01/27 05:45:31 WARN snappy.LoadSnappy: Snappy native library not loaded
	15/01/27 05:45:31 INFO mapred.JobClient: Running job: job_201501270543_0001
	15/01/27 05:45:32 INFO mapred.JobClient:  map 0% reduce 0%

	(중략)

	15/01/27 05:46:10 INFO mapred.JobClient:     Reduce output records=52558
	15/01/27 05:46:10 INFO mapred.JobClient:     Virtual memory (bytes) snapshot=4491788288
	15/01/27 05:46:10 INFO mapred.JobClient:     Map output records=974078

**결과를 확인합니다.**

	hadoop@master:/home/hadoop/HadoopHW$ hadoop dfs -ls /wordFrequency
	
	Found 3 items
	-rw-r--r--   3 hadoop supergroup          0 2015-01-27 05:46 /wordFrequency/_SUCCESS
	drwxr-xr-x   - hadoop supergroup          0 2015-01-27 05:45 /wordFrequency/_logs
	-rw-r--r--   3 hadoop supergroup    1005440 2015-01-27 05:46 /wordFrequency/part-r-00000

다음과 같이 입력함으로써 결과를 정확히 볼 수 있습니다.

	hadoop@master:/home/hadoop/HadoopHW$ hadoop dfs -cat /wordFrequency/part-r-00000

그럼 다음과 같이 표시됩니다.

	(생략)

	zenelophon comedies     1
	zenith comedies 1
	zephyrs tragedies       1
	zir tragedies   2
	zo tragedies    1
	zodiac tragedies        1
	zodiacs comedies        1
	zone tragedies  1
	zounds histories        15
	zounds tragedies        6
	zwaggered tragedies     1

위 메시지는 **["해당 단어" "단어가 속한 문서" "단어가 나온 횟수"]** 를 나타냅니다.

#### **WordCountHomeWork Class 실행**

다음과 같이 입력합니다.
[입력받을 경로] [결과를 받을 경로]의 순서입니다.

	hadoop@master:/home/hadoop/HadoopHW$ hadoop jar target/HadoopHW-0.0.4-SNAPSHOT-jar-with-dependencies.jar HadoopHW.WordCountHomeWork /wordFrequency /wordCount

다음과 같은 메시지가 뜨면서 진행됩니다.

	15/01/27 05:51:05 INFO input.FileInputFormat: Total input paths to process : 1
	15/01/27 05:51:05 INFO util.NativeCodeLoader: Loaded the native-hadoop library
	15/01/27 05:51:05 WARN snappy.LoadSnappy: Snappy native library not loaded
	15/01/27 05:51:06 INFO mapred.JobClient: Running job: job_201501270543_0002
	15/01/27 05:51:07 INFO mapred.JobClient:  map 0% reduce 0%
	
	(중략)
	
	15/01/27 05:51:31 INFO mapred.JobClient:     Reduce output records=52558
	15/01/27 05:51:31 INFO mapred.JobClient:     Virtual memory (bytes) snapshot=1500114944
	15/01/27 05:51:31 INFO mapred.JobClient:     Map output records=52558

**결과를 확인합니다.**

	hadoop@master:/home/hadoop/HadoopHW$ hadoop dfs -ls /wordCount

	Found 3 items
	-rw-r--r--   3 hadoop supergroup          0 2015-01-27 05:51 /wordCount/_SUCCESS
	drwxr-xr-x   - hadoop supergroup          0 2015-01-27 05:51 /wordCount/_logs
	-rw-r--r--   3 hadoop supergroup    1110556 2015-01-27 05:51 /wordCount/part-r-00000

다음과 같이 입력함으로써 결과를 정확히 볼 수 있습니다.

	hadoop@master:/home/hadoop/HadoopHW$ hadoop dfs -cat /wordCount/part-r-00000

그럼 다음과 같이 표시됩니다.

	(생략)
	zealous histories       3 3
	zealous comedies        2 3
	zeals tragedies 1 1
	zed tragedies   1 1
	zenelophon comedies     1 1
	zenith comedies 1 1
	zephyrs tragedies       1 1
	zir tragedies   2 1
	zo tragedies    1 1
	zodiac tragedies        1 1
	zodiacs comedies        1 1
	zone tragedies  1 1
	zounds tragedies        6 2
	zounds histories        15 2
	zwaggered tragedies     1 1

위 결과는 **["단어" "단어가 나온 문서" "문서 내에서 단어가 나온 횟수" "단어가 나온 문서의 수"]** 입니다.

#### **WordTFIDFHomeWork Class 실행**

[분석할 문서] [입력받을 경로] [결과를 받을 경로]로 작성합니다.

	hadoop@master:/home/hadoop/HadoopHW$ hadoop jar target/HadoopHW-0.0.4-SNAPSHOT-jar-with-dependencies.jar HadoopHW.WordTFIDFHomeWork /shakespeare /wordCount /tfIdf

다음과 같은 메시지가 뜨면서 진행됩니다.

	15/01/27 06:05:49 WARN mapred.JobClient: Use GenericOptionsParser for parsing the arguments. Applications should implement Tool for the same.
	15/01/27 06:05:50 INFO input.FileInputFormat: Total input paths to process : 1
	15/01/27 06:05:50 INFO util.NativeCodeLoader: Loaded the native-hadoop library
	15/01/27 06:05:50 WARN snappy.LoadSnappy: Snappy native library not loaded
	15/01/27 06:05:50 INFO mapred.JobClient: Running job: job_201501270543_0003
	15/01/27 06:05:51 INFO mapred.JobClient:  map 0% reduce 0%
	
	(중략)
	
	15/01/27 06:17:02 INFO mapred.JobClient:     Combine output records=0
	15/01/27 06:17:02 INFO mapred.JobClient:     Physical memory (bytes) snapshot=260825088
	15/01/27 06:17:02 INFO mapred.JobClient:     Reduce output records=52558
	15/01/27 06:17:02 INFO mapred.JobClient:     Virtual memory (bytes) snapshot=1499914240
	15/01/27 06:17:02 INFO mapred.JobClient:     Map output records=52558

**결과를 확인합니다.**

	hadoop@master:/home/hadoop/HadoopHW$ hadoop dfs -ls /tfIdf

	Found 3 items
	-rw-r--r--   3 hadoop supergroup          0 2015-01-27 06:17 /tfIdf/_SUCCESS
	drwxr-xr-x   - hadoop supergroup          0 2015-01-27 06:16 /tfIdf/_logs
	-rw-r--r--   3 hadoop supergroup    1412612 2015-01-27 06:16 /tfIdf/part-r-00000

다음과 같이 입력함으로써 결과를 정확히 볼 수 있습니다.

	hadoop@master:/home/hadoop/HadoopHW$ hadoop dfs -cat /tfIdf/part-r-00000

결과는 다음과 같습니다.

	(생략)
	zeals:tragedies 1.6094379124341003
	zed:tragedies   1.6094379124341003
	zenelophon:comedies     1.6094379124341003
	zenith:comedies 1.6094379124341003
	zephyrs:tragedies       1.6094379124341003
	zir:tragedies   3.2188758248682006
	zo:tragedies    1.6094379124341003
	zodiac:tragedies        1.6094379124341003
	zodiacs:comedies        1.6094379124341003
	zone:tragedies  1.6094379124341003
	zounds:histories        10.39720770839918
	zounds:tragedies        4.1588830833596715
	zwaggered:tragedies     1.6094379124341003

**["단어" "단어가 나온 문서" "해당 단어의 중요도"]** 를 의미합니다.
