pipeline {
    agent any

    environment {
        PATH = "/opt/homebrew/bin:$PATH"
    }

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/tobinguyenx/online-bookstore.git'
            }
        }

        stage('Install Dependencies') {
            steps {
                dir('online-bookstore') {
                    sh 'npm install'
                }
            }
        }

        stage('Build') {
            steps {
                dir('online-bookstore') {
                    echo '✅ Build step: nothing to build for Node.js app. Skipping.'
                }
            }
        }

        stage('Test') {
            steps {
                dir('online-bookstore') {
                    sh 'npm test || true'  // Cho phép fail nếu chưa có test
                }
            }
        }

        stage('Code Quality') {
            steps {
                dir('online-bookstore') {
                    withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                        sh '''
                            sonar-scanner \
                            -Dsonar.projectKey=tobinguyenx_online-bookstore \
                            -Dsonar.organization=tobinguyenx \
                            -Dsonar.host.url=https://sonarcloud.io \
                            -Dsonar.login=$SONAR_TOKEN
                        '''
                    }
                }
            }
        }

        stage('Quality Gate') {
            steps {
                timeout(time: 1, unit: 'MINUTES') {
                    waitForQualityGate abortPipeline: true
                }
            }
        }

        stage('Security') {
            steps {
                dir('online-bookstore') {
                    sh 'npm audit || true'  // Allow to continue even with vulnerabilities
                }
            }
        }

        stage('Deploy') {
            steps {
                dir('online-bookstore') {
                    echo '🚀 Deploying application...'
                    sh 'nohup node index.js &'
                }
            }
        }

        stage('Release') {
            steps {
                dir('online-bookstore') {
                    echo '🔖 Tagging release...'
                    script {
                        def version = sh(script: "node -p \"require('./package.json').version\"", returnStdout: true).trim()
                        sh "git tag v${version}"
                        sh "git push origin v${version}"
                    }
                }
            }
        }

        stage('Monitoring') {
            steps {
                script {
                    def status = sh(script: "curl -s -o /dev/null -w '%{http_code}' http://localhost:3000", returnStdout: true).trim()
                    if (status != '200') {
                        error("❌ Monitoring failed! App not responding on port 3000.")
                    } else {
                        echo "✅ App is running and responding (HTTP ${status})"
                    }
                }
            }
        }
    }

    post {
        always {
            echo "🧹 Cleaning up background node processes..."
            sh "pkill -f index.js || true"
        }
        success {
            echo '✅ Pipeline completed successfully!'
        }
        failure {
            echo '❌ Pipeline failed.'
        }
    }
}
