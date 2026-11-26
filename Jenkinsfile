pipeline {
    agent any

    environment {
        // Ensure Maven is in PATH on Windows Jenkins agent if not already in system PATH
        PATH = "C:\\Users\\aloks\\Downloads\\apache-maven-3.10.0-bin\\bin;${env.PATH}"
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn clean compile'
                    } else {
                        bat 'mvn clean compile'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn test'
                    } else {
                        bat 'mvn test'
                    }
                }
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    if (isUnix()) {
                        sh 'mvn package -DskipTests'
                    } else {
                        bat 'mvn package -DskipTests'
                    }
                }
            }
        }
    }

    post {
        success {
            archiveArtifacts allowEmptyArchive: true, artifacts: 'target/*.jar', fingerprint: true
            echo 'Jenkins Pipeline completed successfully!'
        }
        failure {
            echo 'Jenkins Pipeline execution failed!'
        }
    }
}
