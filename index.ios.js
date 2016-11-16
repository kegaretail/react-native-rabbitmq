import React from 'react-native';

export { Connection } from './lib/Connection';
export { Queue } from './lib/Queue';
export { Exchange } from './lib/Exchange';

const ReactNativeRabbitMq = React.NativeModules.ReactNativeRabbitMq;

export default {
  test: (onCallback) => {
    console.log('%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%');
    console.log(ReactNativeRabbitMq);
    return ReactNativeRabbitMq.test(onCallback);
  },
};
