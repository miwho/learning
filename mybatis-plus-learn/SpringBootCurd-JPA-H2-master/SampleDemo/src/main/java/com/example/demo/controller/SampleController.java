package com.example.demo.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.jpa.EmployeeEntity;
import com.example.demo.service.EmployeeService;

import net.bytebuddy.utility.RandomString;

@RestController
@RequestMapping("/employee")
public class SampleController {

	public ConcurrentLinkedQueue<EmployeeEntity> queue = new ConcurrentLinkedQueue<EmployeeEntity>();
	
	public ThreadPoolExecutor threadPool = new ThreadPoolExecutor(5, 5, 20, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
	            new ThreadPoolExecutor.DiscardOldestPolicy());
	
	AtomicInteger allCount = new AtomicInteger(0);
	
	class WorkThread implements Runnable{
		
		EmployeeEntity entity;
		
		WorkThread(EmployeeEntity tmpEntity){
			this.entity = tmpEntity;
		}
		
		@Override
		public void run() {
			try {
				System.out.println("update...[" + allCount.decrementAndGet() + "]...." + entity.getId());
				int res = service.updateEmployeeByFirstName(entity.getFirstName(), "test");
				System.out.println("update resut..." + res);
				System.out.println("search..." + entity.getId());
				long tmpEntity = service.getEmployeeByFirstName(entity.getFirstName());
				System.out.println("search result:" + tmpEntity);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	{
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
						EmployeeEntity entity = queue.poll();
						if (entity == null) {
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							continue;
						} else {
							threadPool.submit(new WorkThread(entity));
							allCount.addAndGet(1);
						}
				}
			}
		}).start();
	}
	
	@Autowired
	EmployeeService service;

	@GetMapping
	public ResponseEntity<List<EmployeeEntity>> getAllEmployeeData() {
		List<EmployeeEntity> list = service.getAllEmployees();
		return new ResponseEntity<List<EmployeeEntity>>(list, new HttpHeaders(), HttpStatus.OK);

	}

	@GetMapping("/{id}")
	public ResponseEntity<EmployeeEntity> getEmpById(@PathVariable("id") Long id) {
		EmployeeEntity entity = service.getEmployeeById(id);

		return new ResponseEntity<EmployeeEntity>(entity, new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping
	public ResponseEntity<EmployeeEntity> createOrUpdateEmployee(@RequestBody EmployeeEntity employee) {
		EmployeeEntity updated = service.createOrUpdateEmployee(employee);
		return new ResponseEntity<EmployeeEntity>(updated, new HttpHeaders(), HttpStatus.OK);
	}

	@PostMapping("/copyAndSave")
	public List<EmployeeEntity> saveAllEmployees(@RequestBody EmployeeEntity employee) {
		List<EmployeeEntity> entityList = copyTime(employee, 1024);
		List<EmployeeEntity> result = service.saveAllEmployees(entityList);
		queue.addAll(result);
		return result;
	}
	
	
	@DeleteMapping("/{id}")
	public HttpStatus deleteById(@PathVariable("id") Long id) {
		service.deleteById(id);
		return HttpStatus.FORBIDDEN;
	}
	
	private List<EmployeeEntity> copyTime(EmployeeEntity employee, int times){
		
		List<EmployeeEntity> entityList = new ArrayList<EmployeeEntity>();
		for(int i=0; i<times; i++) {
			EmployeeEntity tmpEmployee = new EmployeeEntity();
			tmpEmployee.setFirstName(System.nanoTime() + "");
			tmpEmployee.setLastName(createString(1024));
			tmpEmployee.setEmail(employee.getEmail());
			entityList.add(tmpEmployee);
		}
		return entityList;
	}

	private String createString(int size) {
		return RandomString.make(1024);
//		char[] chars = new char[size];
//		// Optional step - unnecessary if you're happy with the array being full of \0
//		Arrays.fill(chars, 'f');
//		return new String(chars);
	}
}
