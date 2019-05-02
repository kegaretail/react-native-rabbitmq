import { Platform } from "react-native"
import RNRabbitMqIos from "./index.ios.js"
import RNRabbitMqAndroid from "./index.android.js"

const RNRabbitMq = Platform.OS === "ios" ? RNRabbitMqIos : RNRabbitMqAndroid

export default RNRabbitMq
