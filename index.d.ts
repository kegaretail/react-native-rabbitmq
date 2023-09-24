export class Connection {
  constructor(config: Object);

  connect(): void;
  close(): void;

  clear(): void;

  handleEvent(event: Object): void;

  on(event: string, callback: (event: Object) => any): void;
  removeon(event: string): void;
}

export class Exchange {
  constructor(connection: Connection, exchange_config: Object);

  publish(message: string, routing_key: string, properties: Object): void;

  delete(): void;

  on(event: string, callback: (event: Object) => any): void;
  removeon(event: string): void;
}

export class Queue {
  constructor(connection: Connection, queue_config: Object, args: Object);

  delete(): void;

  close(): void;

  bind(exchange: Exchange, routing_key: string): void;
  unbind(exchange: Exchange, routing_key: string): void;
  basicAck(delivery_tag: number): void;
  basicNack(delivery_tag: number): void;

  handleEvent(event: Object): void;

  on(event: string, callback: (event: Message) => any): void;
  removeon(event: string): void;
}

interface Message {
  content_type: string;
  delivery_tag: number;
  exchange: string;
  is_redeliver: boolean;
  message: string;
  name: string;
  queue_name: string;
  routing_key: string;
}
