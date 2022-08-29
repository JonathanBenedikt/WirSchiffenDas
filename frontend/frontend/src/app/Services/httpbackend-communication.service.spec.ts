import { TestBed } from '@angular/core/testing';

import { HTTPBackendCommunicationService } from './httpbackend-communication.service';

describe('HTTPBackendCommunicationService', () => {
  let service: HTTPBackendCommunicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(HTTPBackendCommunicationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
