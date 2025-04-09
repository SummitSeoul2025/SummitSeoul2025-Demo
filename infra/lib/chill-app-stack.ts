import * as cdk from 'aws-cdk-lib';
import { Construct } from 'constructs';
import * as lambda from 'aws-cdk-lib/aws-lambda';
import * as apigateway from 'aws-cdk-lib/aws-apigateway';
import * as iam from 'aws-cdk-lib/aws-iam';
import * as secretsmanager from 'aws-cdk-lib/aws-secretsmanager';
import * as s3 from 'aws-cdk-lib/aws-s3';

export class ChillAppStack extends cdk.Stack {
  constructor(scope: Construct, id: string, props?: cdk.StackProps) {
    super(scope, id, props);

    const lambdaRole = new iam.Role(this, 'ChillAppLambdaRole', {
      assumedBy: new iam.ServicePrincipal('lambda.amazonaws.com'),
    });

    lambdaRole.addManagedPolicy(
      iam.ManagedPolicy.fromAwsManagedPolicyName('AmazonBedrockFullAccess')
    );
    
    lambdaRole.addManagedPolicy(
      iam.ManagedPolicy.fromAwsManagedPolicyName('service-role/AWSLambdaBasicExecutionRole')
    );

    const bedrockSecret = new secretsmanager.Secret(this, 'BedrockSecret', {
      secretName: 'chillapp-bedrock-config',
      description: 'Bedrock configuration for ChillApp',
    });

    const chillAppFunction = new lambda.Function(this, 'ChillAppFunction', {
      runtime: lambda.Runtime.NODEJS_18_X,
      handler: 'index.handler',
      code: lambda.Code.fromAsset('../app'),
      role: lambdaRole,
      timeout: cdk.Duration.seconds(30),
      memorySize: 1024,
      environment: {
        'NODE_ENV': 'production',
        'AWS_REGION': this.region,
      },
    });

    bedrockSecret.grantRead(chillAppFunction);

    const api = new apigateway.RestApi(this, 'ChillAppApi', {
      restApiName: 'ChillApp API',
      description: 'API for ChillApp',
      defaultCorsPreflightOptions: {
        allowOrigins: apigateway.Cors.ALL_ORIGINS,
        allowMethods: apigateway.Cors.ALL_METHODS,
      },
    });

    const lambdaIntegration = new apigateway.LambdaIntegration(chillAppFunction);

    const apiResource = api.root.addResource('api');
    apiResource.addMethod('GET', lambdaIntegration);
    apiResource.addMethod('POST', lambdaIntegration);

    const websiteBucket = new s3.Bucket(this, 'ChillAppWebsiteBucket', {
      websiteIndexDocument: 'index.html',
      publicReadAccess: true,
      removalPolicy: cdk.RemovalPolicy.DESTROY,
      autoDeleteObjects: true,
    });

    new cdk.CfnOutput(this, 'ApiGatewayUrl', {
      description: 'API Gateway URL',
      value: api.url,
    });

    new cdk.CfnOutput(this, 'WebsiteUrl', {
      description: 'Website URL',
      value: websiteBucket.bucketWebsiteUrl,
    });
  }
} 