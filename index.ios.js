import React from 'react-native';

const ReactNativeRabbitMq = React.NativeModules.ReactNativeRabbitMq;

export default {
  test: () => {
    return ReactNativeRabbitMq.test();
  },
};
