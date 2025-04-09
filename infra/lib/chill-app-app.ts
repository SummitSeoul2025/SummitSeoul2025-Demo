#!/usr/bin/env node
import * as cdk from 'aws-cdk-lib';
import { ChillAppStack } from './chill-app-stack';

const app = new cdk.App();
new ChillAppStack(app, 'ChillAppStack', {
  env: { 
    account: process.env.CDK_DEFAULT_ACCOUNT, 
    region: process.env.CDK_DEFAULT_REGION || 'ap-northeast-2'
  },
  description: 'ChillApp Infrastructure Stack'
});

app.synth(); 