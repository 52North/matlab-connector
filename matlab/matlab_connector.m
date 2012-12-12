function [ ] = matlab_connector( port, debug, debug_runs )
    % import java classes
    import java.net.ServerSocket
    import java.io.*
    import org.uncertweb.matlab.*
    import org.uncertweb.matlab.value.*

    % sockets
    server_socket = [];
    client_socket = [];

    % handler for matlab data
    handler = MLHandler();
    
    debug_remaining = debug_runs;
    while not(debug) || debug_remaining > 0
        debug_remaining = debug_remaining - 1;
        try
            fprintf(1, 'Waiting for client to connect on port : %d...\n', port);
            
            server_socket = ServerSocket(port);
            client_socket = server_socket.accept;
            
            fprintf(1, 'Client connected\n');
            
            output_stream = client_socket.getOutputStream;
            input_stream = client_socket.getInputStream;
            
            request = handler.parseRequest(input_stream);
            
            try
                eval(request.toEvalString());
                mlresult = MLResult();
                for i = 1:request.getResultCount()
                    result = eval(['result', num2str(i)]);
                    mlresult.addResult(parse_value(result));
                end            
                handler.outputResult(mlresult, output_stream);
            catch exception
                disp(exception.identifier)
                disp(exception.message)
                mlexception = MLException(exception.message);
                handler.outputException(mlexception, output_stream);
            end
            
            % close streams
            output_stream.close;
            input_stream.close;
            client_socket.close;
            server_socket.close;  
        catch exception
            % io problem, json serialize/deserialize problem...
            disp(exception.identifier)
            disp(exception.message)
            
            if ~isempty(client_socket)
                client_socket.close
            end
            
            if ~isempty(server_socket)
                server_socket.close
            end
            
            pause(1); 
        end
    end
end

function [ parsed_value ] = parse_value( value )
    % import java classes
    import org.uncertweb.matlab.value.*
    
    % check type
    if ischar(value)
        parsed_value = MLString(value);
    elseif iscell(value)
        % loop through cell values, parse_result on each, add to
        vsize = size(value, 2);
        array = javaArray('org.uncertweb.matlab.value.MLValue', vsize);
        for i = 1:vsize
            array(i) = parse_value(value{i});
        end
        parsed_value = MLCell(array);
    elseif isstruct(value)
        % loop through struct fields, parse result on each value
        parsed_value = MLStruct();
        names = fieldnames(value);
        for i = 1:size(names, 1)
            name = names{i};
            parsed_value.setField(name, parse_value(value.(name)));
        end        
    else
        vsize = size(value);
        rows = vsize(:,1);
        cols = vsize(:,2);
        if rows == 1
            if cols == 1
                parsed_value = MLScalar(value);
            else
                parsed_value = MLArray(value);
            end
        else
            parsed_value = MLMatrix(value);
        end
    end
end
