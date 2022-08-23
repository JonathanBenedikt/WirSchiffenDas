import { TestBed } from '@angular/core/testing';

import { AnaylserCommunicationService } from './anaylser-communication.service';

describe('AnaylserCommunicationService', () => {
  let service: AnaylserCommunicationService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(AnaylserCommunicationService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
