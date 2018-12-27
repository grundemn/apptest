
echo "Starting Pipeline for ...."
 def giturl = "https://github.com/grundemn/apptest.git"
 def mvnCmd = "mvn -s configuration/cicd-settings-nexus3.xml"
 
pipeline {
    
 agent { label 'maven' }
    
    stages {
     stage('Build Code') {
        steps {
		 script {
			 git url: "${giturl}"
              sh "${mvnCmd} install -DskipTests=true"
			   echo "Maven Build Complete"
					}
                }
            }
     stage('Code Coverage') {
        steps {
		 script {
          sh "${mvnCmd} sonar:sonar -Dsonar.host.url=http://sonarqube:9000 -DskipTests=true"
		   echo "Code Test Coverage Complete"
					}
				}
			}
     stage('Unit Tests') {
        steps {
			script {
            sh "${mvnCmd} test"
			step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
			  echo "Maven Unit test Complete"
					}
				}
			}
     stage('Archive App') {
		steps {
            script {
//			 sh "${mvnCmd} deploy -DskipTests=true -P nexus3"
             echo "Archive app complete"
					}	
				}
			}
     stage('Image Build') {
        steps {
		 script {
			sh "echo Building Image from Jar File"
			 sh """
			  rm -rf oc-build && mkdir -p oc-build/deployments
			    for t in \$(echo "jar" | tr ";" "\\n"); do
                 cp -rfv ./target/*.\$t oc-build/deployments/ 2> /dev/null || echo "No \$t files"
			      done
				"""
			 openshift.withCluster('dev') {
              openshift.withProject( 'midtier' ) {
               openshift.selector("bc", "boot-isocode-xjxg066").startBuild("--from-dir=oc-build/deployments", "--wait=true")
			    sh "echo Building Image Complete"
                            }
                        }
                    }
                }
            }
     stage('Deploy DEV') {
        steps {
			script {
			 echo "Starting Dev Deploy"
			 openshift.withCluster('dev') {
               openshift.withProject( 'midtier' ) {
                try { openshift.selector("dc/boot-isocode-xjxg066").rollout().latest() } catch (err) { }
                 //Verify deployment to dev code here
				 echo "Deploy to Dev complete"
							}
						}
					}
				}
			}
	
     stage('Deploy QA') { 
	  agent { label 'skopeo' }
        steps {
            script {
			 echo "Promoting new image to QA registry using Skopeo..."
			  openshift.withProject('cicd') {
			   withCredentials([
				usernamePassword(credentialsId: "qa-reg-token", usernameVariable: "QA_USER", passwordVariable: "QA_PWD"),
				usernamePassword(credentialsId: "dev-reg-token", usernameVariable: "DEV_USER", passwordVariable: "DEV_PWD")
				 ]) {
                  sh "skopeo copy docker://docker-registry.default.svc:5000/midtier/boot-isocode-xjxg066:latest docker://docker-registry-default.ospqa.gcom.grainger.com/midtier/boot-isocode-xjxg066:latest --src-creds \"$DEV_USER:$DEV_PWD\" --dest-creds \"$QA_USER:$QA_PWD\" --src-tls-verify=false --dest-tls-verify=false"
                    }
				}
				echo "Skopeo update complete"
            openshift.withCluster('qa') {
              openshift.withProject( 'midtier' ) {
               openshift.selector("dc", "boot-isocode-xjxg066").rollout().latest();
					echo "Testing QA deployment"
                  //Verify deploy to QA code here
								}
							}
						}
					}
				}
		
     stage('Deploy PROD LF') {
	  agent { label 'skopeo' }
        steps {
            mail (
            to: 'jeff.grundeman@grainger.com',
            subject: "Job '${JOB_NAME}' (${BUILD_NUMBER}) is waiting for input",
             body: "Please go to ${BUILD_URL} and verify the build");
                 timeout(time:15, unit:'MINUTES') {
                      input message: "Promote to Prod LF?", ok: "Promote"
                 }
                script {
				 withCredentials([
					usernamePassword(credentialsId: "qa-reg-token", usernameVariable: "QA_USER", passwordVariable: "QA_PWD"),
					usernamePassword(credentialsId: "prodlf-reg-token", usernameVariable: "PROD_USER", passwordVariable: "PROD_PWD")
					]) {
                      sh "skopeo copy docker://docker-registry-default.ospqa.gcom.grainger.com/midtier/boot-isocode-xjxg066:latest docker://docker-registry-default.ospprodlf.gcom.grainger.com/midtier/boot-isocode-xjxg066:latest  --src-creds \"$QA_USER:$QA_PWD\" --dest-creds \"$PROD_USER:$PROD_PWD\" --src-tls-verify=false --dest-tls-verify=false"
                        }
            openshift.withCluster('prod-lf') {
             echo "Starting Prod LF rollout"
               openshift.withProject( 'midtier' ) {
                openshift.selector("dc", "boot-isocode-xjxg066").rollout().latest();
                 //Verify deploy to Prod here
								}
							}
						}
					}
				}
	 
    stage('Deploy PROD T5') {
	 agent { label 'skopeo' }
        steps {
          timeout(time:120, unit:'MINUTES') {
          input message: "Promote to Prod T5?", ok: "Promote"
                 }
			script {
				 withCredentials([
					usernamePassword(credentialsId: "qa-reg-token", usernameVariable: "QA_USER", passwordVariable: "QA_PWD"),
					usernamePassword(credentialsId: "prodt5-reg-token", usernameVariable: "PROD_USER", passwordVariable: "PROD_PWD")
					]) {
                      sh "skopeo copy docker://docker-registry-default.ospqa.gcom.grainger.com/midtier/boot-isocode-xjxg066:latest docker://docker-registry-default.ospprodt5.gcom.grainger.com/midtier/boot-isocode-xjxg066:latest  --src-creds \"$QA_USER:$QA_PWD\" --dest-creds \"$PROD_USER:$PROD_PWD\" --src-tls-verify=false --dest-tls-verify=false"
                        }
            openshift.withCluster('prod-t5') {
			 echo "Staring Prod T5 rollout"
               openshift.withProject( 'midtier' ) {
                openshift.selector("dc", "boot-isocode-xjxg066").rollout().latest();
              
                  //verify deploy to prod here
										}
									}
								}
							}      
						}
					}
				}
