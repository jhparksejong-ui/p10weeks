
pipeline {
    agent any

    // 1. 환경 변수 설정 (이전에 Execute shell에 넣었던 부분)
    environment {
        JAVA_HOME = '/usr/lib/jvm/java-17-amazon-corretto'
        PATH = "${JAVA_HOME}/bin:${env.PATH}"
    }

    stages {
        // Stage 1: 깃허브에서 최신 코드 가져오기
        stage('Git Clone') {
            steps {
                git branch: 'main', url: 'https://github.com/jhparksejong-ui/p10weeks.git'
            }
        }

        stage('SonarQube Security Analysis') {
            steps {
                echo "🔎 자체 구축 SonarQube 정적 코드 보안 분석 시작..."
                sh '''
                ./gradlew sonar \
                  -Dsonar.projectKey=p10weeks-local \
                  -Dsonar.host.url=http://127.0.0.1:9000 \
                  -Dsonar.login=sqa_91d4d28874a2aa1893f38bcfabed3483e07cd383
                '''
            }
        }

        // Stage 2: 코드 빌드하기
        stage('Build') {
            steps {
                echo "📦 빌드를 시작합니다..."
                sh 'chmod +x gradlew'
                sh './gradlew clean build -x test'
            }
        }

        // Stage 3: 스프링 서버로 전송 및 실행 (Publish Over SSH 플러그인 연동)
        stage('Deploy to Spring Server') {
            steps {
                echo "🚀 스프링 서버로 배포를 시작합니다..."
                
                // 프리스타일에서 설정했던 'Send build artifacts over SSH' 내용과 100% 동일한 파이프라인 문법입니다.
                sshPublisher(publishers: [
                    sshPublisherDesc(
                        configName: 'spring-server', // 기존에 젠킨스 시스템 설정에 등록해둔 SSH 서버 이름
                        transfers: [
                            sshTransfer(
                                sourceFiles: 'build/libs/*-SNAPSHOT.jar',
                                removePrefix: 'build/libs',
                                remoteDirectory: 'app/step1',
                                execCommand: '''
                                    cd /home/ec2-user/app/step1
                                    
                                    # 1. 기존 프로세스 종료
                                    sudo fuser -k 8080/tcp || true
                                    sleep 2
                                    
                                    # 2. 절대 경로로 무중단 백그라운드 실행
                                    BUILD_ID=dontKillMe nohup java -jar /home/ec2-user/app/step1/*-SNAPSHOT.jar > /home/ec2-user/app/step1/spring.log 2>&1 &
                                '''
                            )
                        ],
                        verbose: true
                    )
                ])
            }
        }
    }
}

