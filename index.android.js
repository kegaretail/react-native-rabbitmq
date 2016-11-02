import React from 'react-native';

const ReactNativeRabbitMq = React.NativeModules.ReactNativeRabbitMq;

export default {
  test: (onSuccess, onFailure) => {
    return ReactNativeRabbitMq.test(onSuccess, onFailure);
  },
};
