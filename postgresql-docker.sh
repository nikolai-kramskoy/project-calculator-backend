docker run -d \
        --name pg \
        -p 127.0.0.1:5432:5432 \
	      -e POSTGRES_USER=dev \
        -e POSTGRES_PASSWORD=239u8rqw781sadhy278ty78sdgf8 \
        -e POSTGRES_DB=project-calculator-dev \
        postgres:16.2
