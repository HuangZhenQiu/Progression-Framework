Progression-Framework
=====================

Internet of things bring several challenges in computer science community. It includes:
* Connectivity
* Interoperability
* Energy Efficiency
* Fault Tolerant
* Intelligence

In our former works, service oriented methodology is applied in Wukong to abstract capability of  various sensors and actuators as WuClass. Wukong profiling framework implemented in Darjeeling virtual machine is designed for cross platform device discovery, identify capabilities (WuClasses) on devices, and reprogramming. To create a Wukong application, these WuClass modules can be composed into a flow base program, mapped, deployed onto WuDevice through zwave. Multiple Network transportation protocol in Wukong gateway is designed to tackle with the problem of diversity of low power wireless communication (Zwave, Xbee, Low Power wifi).

Wukong policy framework is designed to apply energy policy and fault tolerant policy, when master mapping components into devices. We achieve the goal of energy efficient by modeling the problem into a quartic programming problem. On the other hand, sensor probabilistic model and  replica can be used in advance when we apply fault tolerant policy.

Wukong Progression framework aims to bring intelligence into wukong application. It’s first release is designed to help regular FBP reactable to external factor changes, such as user’s location, behavior and weather and etc. Within the framework, progression server is implemented as a UDP device in Wukong ecosystem. The server has WKPF enabled, which means it will be assigned an network id after adding into master through UDP gateway. The program components, which are PrClasses, loaded during initialization of progression server can be discovered by master through WKPF. Accordingly, the component instances, which are PrObjects created one for each PrClass during initialization can be used for constructing flow based program.

Since progression server implemented whole stack of MNTP and WKPF protocols, it is an actually a wukong on regular JVM. Thus, modules within progression server can leverage all the capability of JVM to talk to external world through communication channels, such XMPP, MQTT, REST, DB Connection and etc.

In the coming release II of Progression Framework, we will enable the feature of Time Series Data Buffer, Operators API, and learning pipeline. In the end, we will bring the concept of intelligent streaming processing in IOT.


## Setup
Progression Server is built by Gradle. Please install gradle 2.4 in advance. After that, just simple git clone the project into your local file system. There are several simple steps to start the server.

### Configuration
The structure of project confirms to gradle standards. For now, the whole project only contains one sub project which called framework. For now, the config files for server and log4j are located in the path of `framework/src/main/resource`. You may update the setting of xmpp, gateway and wukong server in the `config.properties`. 


### Build Project
After config the server to your Wukong Ecosystem setting. You may run the command below to build the project.

    gradle buildServer

This command will build and pack the frameowork with all of its dependencies into framework-all.jar, and copy it into
bin folder.


### Run Server 
To start the server, you just simply go to the bin folder, and run the command below.

    sh run.sh


## Programming in Server
In this release, we provide a programming paradigm to design PrClass, which is an extension of WuClass. In the server, we provide an abstraction for PrClass to integrate and control WuObjects, call external service or exchange information through pub/sub, and trigger remapping. Below, we provide some examples to show you how to magically make FBP more powerful.



### Code Convention
If you want to create a PrClass in progression server, you need to create a sub package in `edu.uci.eecs.wukong.prclass`. For example, you are going to build a smart switch, you should create the package `edu.uci.eecs.wukong.prclass.switch`. Any new PrClass should extends from PrClass. A PrClass is the place to define I/O of the smart component, including input/output properties, topics that want to subscribe, and the extensions that implement the processing logic. In this release, we only support the `ProgressionExtension`. For any features of release I, you can put them in a subclass of `ProgressionExtension`

### Hello World PrClass

    @WuClass(id = 2001)
    Class SmartSwitch extends PrClass {
        @WuProperty(name = 'input', id = 1, type = PropertyType.Input, dtype = DataType.Channel)
        Public SmartSwitch() {
            super("SmartSwitch");
        }
        @Override
        public list<Extension> registerExtension() {
            List<Extension> extensions = new ArrayList<Extension>();
            extensions.add(new ContextProgressionExtension(this));
            return extensions;
        }
    }
    
    public class ContextProgressionExtension extends AbstractProgressionExtension implements Channelable  {
      	public ContextProgressionExtension(PrClass plugin) {
		    super(plugin);
	    }
        public void execute(ChannelData data) {
            if (data.getNpp().getPropertyId() == 1) {
                System.out.println("Hello Word!");
	        }
	    }
    }

To make the hello world PrClass usable in composing a FBP, the SmartSwitch need to be defined consistently in `standardlibarary.xml`. In the example above, the smart switch's WuClass ID is 2001. It has an input property whose data will be put in channel once progression server receive a wkpf message for that property. The PrClass will register an extension which is called `ContextProgressionExtension`. Every channel data will forward to the extension, thus the execute will be called immediately to print `Hello World!` in console.


### Advance Features of Release I
In the release, you can achieve any combination of three features below through implements particular interface and motify a little bit of the example above. 

* Control Other WuObjects
* Integrate External Services
* Remapping

#### Control Other WuObjects

#### Integrate External Services

#### Reconfiguration and Remapping






  









