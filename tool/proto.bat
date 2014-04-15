cd ..
protoc --proto_path=network/src/main/java/edu/uci/eecs/wukong/network/netty/proto --java_out=network/src/main/java network/src/main/java/edu/uci/eecs/wukong/network/netty/proto/DataService.proto
protoc --proto_path=network/src/main/java/edu/uci/eecs/wukong/network/netty/proto --java_out=network/src/main/java network/src/main/java/edu/uci/eecs/wukong/network/netty/proto/ControlService.proto
