# Kubenetes Demo

DRAFT!

## Introduction

This project aims to build an environment that contains various important components relevant for the Certified 
Kuberentes Application Developer exam (**CKAD**). Inspiration comes from Nigel Poulton and Dan who have a fantastic
CKAD Path on Pluralsight to prepare for the exam. After taking the courses I felt like creating a project as excercise
that contain various of its components the course discusses.

## Demo Purpose
**Problem**: It feels embarrassing to forget each other's name. Therefore we need a way to memorize.

**Solution**: Build a REST API available publicly available. As we don't want to forget names we hear, we persist them.

**Goal**: After this exercise you'll never forget a name again.

## Components 
The following comes across:
1. Building OCI images
2. Application Deployment (simple and strategies as Canary and Blue/Green)
3. Init containers
4. Services
5. Network Policies
6. Ingress
7. ConfigMap
8. Secure configuration

## Prerequisites

### Knowledge
1. Basic Kubernetes Knowledge
2. Basic Docker Knowledge

### Tools
1. Java 21
2. Docker Desktop
3. Kubernetes 1.xx.xx

## Let's Go!

// todo! Describe the steps in terms of questions.

1. Build docker images voor app 
2. Maak deployment file (Andere strategies in andere branches) + config + secure config
3. Maak service file
4. Zet Ingress op (incl controller)

### Task 1
Create a docker image for the Kube-demo app. 

```yaml
# add to services of docker-compose
kube-demo:
  image: kube-demo
  build: .
  ports:
    - "8080:8080"
```
and run `
```shell
$ docker compose build
``` 
or use 
```shell
$ docker build kube-demo -it .
```

### Task 2
Create an alias to simplify your commands

```shell
$ alias k=kubectl
```

Create a kubernetes namespace for your demo environment.

```shell
$ k create ns kd
```

and set the current context to that namespace
```shell
$ k config set-context --current --namespace=kd
```
### Task 3 
Create a deployment yaml **file** `kube-deploy.yaml` for the Kube-demo app.
The app runs on port 8080.

```shell
$ k create deploy kube-deploy --image=kube-demo --dry-run=client --port=8080 -o yaml > kube-deploy.yaml
```

Apply the deployment, make sure it uses the local image `kube-demo`

```shell
$ k apply -f k8s/kube-deploy.yml
```
To ensure that the local image is being used there is a need to apply a certain spec to the deployment.
Add th following to `{.spec.template.containes}`:
```yaml
imagePullPolicy: IfNotPresent
```


Verify if pods are running
```shell
$ k get pods
```
As we are in the namespace `kd` there is no need to specify the namespace. Otherwise, add the namespace flag. Similarly to
other commands during this exercise.
You should see a running pod, based on our deployment.
The logs of the pod indicate there is a connection issue with mongo.
```shell
$ k logs [pod name]
```
Let's fix that right away!
### Task 4
First of all, let's create a deployment for our Mongo database. Without a running pod, no db. There is an option to add
the Mongo container to the pod, however this violates **Separation of Concerns** principle. Each pod should have its own
task. Apart from a helper container, this means different tasks run in different pods. The task of the app is to expose 
the API. The storage of the data is another task. So Mongo runs in another pod.

```shell
$ k create deploy mongo-deploy --image=mongo:5.0.24 --port 27017 --dry-run=client -o yaml > mongo-deploy.yaml
```
and run it:
```shell
$ k apply -f mongo-deploy.yaml 
```
Verify if the new pod is running. You should see two pods in the `kd` namespace now.

```shell
k expose deployment mongo-deploy --port 27017 --target-port=27017 --name=mongo-svc -o yaml > k8s/mongo-svc.yaml 
```
Verify if svc in up (`k get svc` )and check by describe the endpoints (`k describe svc mongo-svc`). If it has endpoints it indicates the pods created by the mongo-deploy 
are attached to the service

k create cm kube-config --from-literal=MONGO_DB_HOST=10.98.125.244 -o yaml > kube-config.yaml
rebuild image kube-demo after adjusting  the app props file
config maps have to be applied before pod creation. So delete deployment, and apply again

do same for mongo. As it requires a root use to be set up in the container. Otherwise, auth fails

## Task 5
Verify if app works with db
shell in to the kube-demo pod:
```shell
$ k exec [your pod] -it -- sh
```
inside the container use curl (if not installed use `apt-get install curl`):
curl localhost:8080/anId
this results in `200 OK` with  **NAME NOT FOUND!**. 
So all good. connection working

## Task 6
Create a public endpoint for the app.
1. create a svc
2. use ingress
   3. might need ingress controller

create service
```shell
$ k expose deployment kube-deploy --name=kube-svc --port=8080 --target-port=8080 --dry-run=client -o yaml > k8s/kube-svc.yaml
```

apply service
```shell
$  k apply -f k8s/kube-svc.yaml 
```
set up ingress create a file in k8s folder called `kube-ingress.yaml`
copy minimal from docs https://kubernetes.io/docs/concepts/services-networking/ingress/
apply
```shell
$ k apply -f k8s/kube-ingress.yaml
```
verify via describe ingress kube-ingress if things seem ok
```text
Name:             kube-ingress
Labels:           <none>
Namespace:        kd
Address:          localhost
Ingress Class:    nginx
Default backend:  <default>
Rules:
  Host        Path  Backends
  ----        ----  --------
  *           
              /   kube-svc:8080 (10.1.0.255:8080)
Annotations:  nginx.ingress.kubernetes.io/rewrite-target: /
Events:
  Type    Reason  Age              From                      Message
  ----    ------  ----             ----                      -------
  Normal  Sync    5s (x3 over 3m)  nginx-ingress-controller  Scheduled for sync
```
Now localhost is mapped to the internal service 
```shell
$ curl localhost/something
```
now results in **NAME NOT FOUND!**

BAM!

You are now able to remember names!
POST http://localhost?name=[your name]

returns an ID

GET http://localhost/[RETURNED_ID]

returns [your name]!

### Task 7
set up secret file for mongo db password